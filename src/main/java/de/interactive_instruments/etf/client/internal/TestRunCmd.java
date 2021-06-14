/**
 * Copyright 2019-2020 interactive instruments GmbH
 *
 * Licensed under the EUPL, Version 1.2 or - as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 * https://joinup.ec.europa.eu/software/page/eupl
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 */
package de.interactive_instruments.etf.client.internal;

import java.net.URI;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import org.json.JSONObject;

import de.interactive_instruments.etf.client.*;

/**
 * @author Jon Herrmann ( herrmann aT interactive-instruments doT de )
 */
class TestRunCmd implements TestRun {

    public final static String PATH = "TestRuns/";

    private static class RestultProxy implements TestRunResult {

        private final TestRun currentTestRun;
        private TestRunResult result;
        private final LocalDateTime startDate;

        public RestultProxy(final TestRun currentTestRun) {
            this.currentTestRun = currentTestRun;
            this.startDate = LocalDateTime.now();
        }

        @Override
        public List<String> logEntries() throws RemoteInvocationException {
            if (result != null) {
                return result.logEntries();
            }
            return currentTestRun.logEntries();
        }

        @Override
        public LocalDateTime startDate() {
            if (result != null) {
                return result.startDate();
            }
            return startDate;
        }

        @Override
        public long duration() {
            if (result != null) {
                return result.duration();
            }
            return Duration.between(this.startDate, LocalDateTime.now()).toMillis();
        }

        @Override
        public Collection<TestTaskResult> testTaskResults() {
            if (result != null) {
                return result.testTaskResults();
            }
            return Collections.emptyList();
        }

        @Override
        public void delete() throws RemoteInvocationException {
            if (result != null) {
                result.delete();
            }
        }

        @Override
        public Iterator<TestResult> iterator() {
            if (result != null) {
                return result.iterator();
            }
            return Collections.emptyIterator();
        }
    }

    private final RestultProxy proxy = new RestultProxy(this);
    private final List<String> logEntries = new ArrayList<>();
    private double progress = 0;
    private Future<TestRunResult> future;
    private boolean canceled = false;
    private final TestRunObserver testRunObserver;
    private DeleteRequest deleteRequest;
    private final InstanceCtx ctx;
    private Exception exception;

    private TestRunCmd(final InstanceCtx ctx, final TestRunObserver testRunObserver) {
        this.testRunObserver = testRunObserver;
        this.ctx = ctx;
    }

    private TestRunResult resultProxy() {
        return this.proxy;
    }

    void finished(final TestRunResult result) {
        this.proxy.result = result;
        if (this.testRunObserver != null) {
            this.testRunObserver.testRunFinished(result);
        }
    }

    void exception(final Exception exception) {
        if (exception != null) {
            this.exception = exception;
        }
        if (this.testRunObserver != null) {
            this.testRunObserver.exceptionOccurred(exception);
        }
    }

    void updateProgress(final double progress, final List<String> logEntries) {
        this.progress = progress;
        this.logEntries.addAll(logEntries);
    }

    private DeleteRequest deleteRequest() {
        return deleteRequest;
    }

    private void injectFuture(final Future<TestRunResult> future) {
        this.future = future;
    }

    @Override
    public void cancel() throws RemoteInvocationException {
        if (!canceled) {
            canceled = true;
            this.deleteRequest.delete();
        }
    }

    @Override
    public double progress() {
        return this.progress;
    }

    @Override
    public boolean finished() {
        return this.progress >= 1.0;
    }

    @Override
    public List<String> logEntries() {
        return this.logEntries;
    }

    private String start(final Collection<ExecutableTestSuite> executableTestSuites, final TestObject testObject,
            final RunParameters parameters)
            throws RemoteInvocationException {
        final JSONObject startTestRequest = new JSONObjectWithOrderedAttributes();
        startTestRequest.putOnce("label", "ETF-client " + ctx.sessionId + " run " + ctx.requestNo());
        final Collection<String> executableTestSuiteIds = executableTestSuites.stream().map(ItemMetadata::eid)
                .collect(Collectors.toList());
        startTestRequest.put("executableTestSuiteIds", executableTestSuiteIds);
        startTestRequest.put("arguments",
                RunParametersImpl.toJson(parameters, EtsCollectionCmd.mergeRunParameters(executableTestSuites)));
        startTestRequest.put("testObject", ((AdHocTestObjectImpl) testObject).toJson());

        return start(startTestRequest);
    }

    private String start(final TestRunTemplate testRuntemplate, final TestObject testObject, final RunParameters parameters)
            throws RemoteInvocationException {
        final JSONObject startTestRequest = new JSONObjectWithOrderedAttributes();
        startTestRequest.put("testRunTemplateId", testRuntemplate.eid());
        startTestRequest.putOnce("label", "ETF-client " + ctx.sessionId + " run " + ctx.requestNo());
        startTestRequest.put("arguments", RunParametersImpl.toJson(parameters, testRuntemplate.parameters()));
        startTestRequest.put("testObject", ((AdHocTestObjectImpl) testObject).toJson());

        return start(startTestRequest);
    }

    private String start(final JSONObject startTestRequest) throws RemoteInvocationException {
        final JsonPostRequest jsonPostRequest = new JsonPostRequest(
                URI.create(ctx.baseUrl.toString() + "/" + PATH.substring(0, PATH.length() - 1)), ctx);
        final JSONObject testRunCreatedResponse = jsonPostRequest.post(startTestRequest);
        final String eid = testRunCreatedResponse.getJSONObject("EtfItemCollection").getJSONObject("testRuns")
                .getJSONObject("TestRun").getString("id");
        this.deleteRequest = new DeleteRequest(
                URI.create(ctx.baseUrl.toString() + "/" + PATH + eid), ctx);
        return eid;
    }

    @Override
    public TestRunResult result() throws IllegalStateException, ExecutionException {
        if (canceled) {
            throw new IllegalStateException("Test Run has been canceled");
        }
        if (exception != null) {
            throw new ExecutionException(exception);
        }
        try {
            return future.get();
        } catch (InterruptedException e) {
            throw new ExecutionException(e);
        } catch (CancellationException c) {
            throw new IllegalStateException("Test Run has been canceled");
        }
    }

    static TestRun start(final InstanceCtx ctx, final ExecutorService executor,
            final Collection<ExecutableTestSuite> selectedExecutableTestSuites,
            final Iterable<ExecutableTestSuite> allExecutableTestSuites,
            final TestObject testObject,
            final TestRunObserver testRunObserver,
            final RunParameters parameters) throws RemoteInvocationException {
        final TestRunCmd testRunCmd = new TestRunCmd(ctx, testRunObserver);
        final String eid = testRunCmd.start(selectedExecutableTestSuites, testObject, parameters);
        return prepareResultStructure(ctx, executor, allExecutableTestSuites, testRunCmd, eid);
    }

    static TestRun start(final InstanceCtx ctx, final ExecutorService executor,
            final TestRunTemplate selectedTestRunTemplate,
            final Iterable<ExecutableTestSuite> allExecutableTestSuites,
            final TestObject testObject,
            final TestRunObserver testRunObserver,
            final RunParameters parameters) throws RemoteInvocationException {
        final TestRunCmd testRunCmd = new TestRunCmd(ctx, testRunObserver);
        final String eid = testRunCmd.start(selectedTestRunTemplate, testObject, parameters);
        return prepareResultStructure(ctx, executor, allExecutableTestSuites, testRunCmd, eid);
    }

    private static TestRun prepareResultStructure(final InstanceCtx ctx, final ExecutorService executor,
            final Iterable<ExecutableTestSuite> allExecutableTestSuites,
            final TestRunCmd testRunCmd, final String eid) {
        final Map<String, AbstractResult.ResultCtx> etsMap = new HashMap<>();
        for (final ExecutableTestSuite executableTestSuite : allExecutableTestSuites) {
            etsMap.put(executableTestSuite.eid(), new AbstractResult.ResultCtx(
                    ctx, executableTestSuite));
        }
        final TestRunResultCmd testRunResultCmd = new TestRunResultCmd(
                ctx, eid, etsMap, testRunCmd.deleteRequest());
        final TestRunMonitor statusQuery = new TestRunMonitor(ctx, testRunCmd, eid, testRunResultCmd);
        final Future<TestRunResult> future = executor.submit(statusQuery, testRunCmd.resultProxy());
        testRunCmd.injectFuture(future);
        return testRunCmd;
    }
}
