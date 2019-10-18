/**
 * Copyright 2017-2019 European Union, interactive instruments GmbH
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
 *
 * This work was supported by the EU Interoperability Solutions for
 * European Public Administrations Programme (http://ec.europa.eu/isa)
 * through Action 1.17: A Reusable INSPIRE Reference Platform (ARE3NA).
 */
package de.interactive_instruments.etf.client.internal;

import static de.interactive_instruments.etf.client.internal.TestRunCmd.PATH;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import org.json.JSONObject;

import de.interactive_instruments.etf.client.*;

/**
 * @author Jon Herrmann ( herrmann aT interactive-instruments doT de )
 */
class TestRunResultCmd implements CreateResultCmd {

    private final static String SUFFIX = ".json";
    private final static String LOG_SUFFIX = "/log";

    private final String testRunEid;
    private final Map<String, AbstractResult.ResultCtx> preparedResultCtx;
    private final JsonGetRequest jsonGetRequest;
    private final SimpleGetRequest logFileRequest;
    private final DeleteRequest deleteRequest;

    private static class DefaultTestRunResult implements TestRunResult {

        private final SimpleGetRequest logFileRequest;
        private final DeleteRequest deleteRequest;
        private final LocalDateTime startDate;
        private final List<TestTaskResult> testTaskResults;

        public DefaultTestRunResult(final JSONObject testRun, final Collection<JSONObject> testTasks,
                final Map<String, AbstractResult.ResultCtx> preparedResultCtx,
                final SimpleGetRequest logFileRequest, final DeleteRequest deleteRequest) {
            this.logFileRequest = logFileRequest;
            this.deleteRequest = deleteRequest;
            startDate = LocalDateTime.from(DateTimeFormatter.ISO_DATE_TIME.parse(testRun.getString("startTimestamp")));
            testTaskResults = new ArrayList<>();
            for (final JSONObject testTaskResult : testTasks) {
                final AbstractResult.ResultCtx resultCtx = preparedResultCtx.get(
                        testTaskResult.getJSONObject("resultedFrom").getString("ref"));
                testTaskResults.add(new TestTaskResultImpl(Objects.requireNonNull(resultCtx,
                        "Executable Test Suite not found").newChild(testTaskResult)));
            }

        }

        @Override
        public List<String> logEntries() throws RemoteInvocationException {
            return this.logFileRequest.query().lines().collect(Collectors.toList());
        }

        @Override
        public LocalDateTime startDate() {
            return this.startDate;
        }

        @Override
        public long duration() {
            long duration = 0;
            for (final TestTaskResult testTaskResult : testTaskResults) {
                duration += testTaskResult.duration();
            }
            return duration;
        }

        @Override
        public Collection<TestTaskResult> testTaskResults() {
            return this.testTaskResults;
        }

        @Override
        public void delete() throws RemoteInvocationException {
            this.deleteRequest.delete();
        }

        @Override
        public Iterator iterator() {
            return new AbstractResult.TestResultIterator(this.testTaskResults.iterator());
        }
    }

    public TestRunResultCmd(final InstanceCtx ctx, final String testRunEid,
            final Map<String, AbstractResult.ResultCtx> preparedResultCtx, final DeleteRequest deleteRequest) {
        this.testRunEid = testRunEid;
        this.preparedResultCtx = preparedResultCtx;
        jsonGetRequest = new JsonGetRequest(
                URI.create(ctx.baseUrl.toString() + "/" + PATH + testRunEid + SUFFIX), ctx);
        logFileRequest = new SimpleGetRequest(
                URI.create(ctx.baseUrl.toString() + "/" + PATH + testRunEid + LOG_SUFFIX), ctx);
        this.deleteRequest = deleteRequest;

    }

    @Override
    public TestRunResult create() {
        final JSONObject result;
        try {
            result = jsonGetRequest.query();
        } catch (RemoteInvocationException e) {
            throw new IllegalStateException(e);
        }
        final JSONObject testRun = result.getJSONObject("EtfItemCollection").getJSONObject(
                "testRuns").getJSONObject("TestRun");
        final Collection<JSONObject> testTasks = new JSONObjectOrArray(result.getJSONObject("EtfItemCollection").getJSONObject(
                "referencedItems").getJSONObject("testTaskResults")).get("TestTaskResult");
        return new DefaultTestRunResult(testRun, testTasks, this.preparedResultCtx,
                this.logFileRequest, this.deleteRequest);
    }
}
