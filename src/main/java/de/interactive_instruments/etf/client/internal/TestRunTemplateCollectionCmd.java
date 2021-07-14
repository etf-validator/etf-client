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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.json.JSONArray;
import org.json.JSONObject;

import de.interactive_instruments.etf.client.*;

/**
 * @author Jon Herrmann ( herrmann aT interactive-instruments doT de )
 */
public class TestRunTemplateCollectionCmd {

    private final static String PATH = "/TestRunTemplates";
    private final ExecutorService executor = Executors.newCachedThreadPool();
    private final InstanceCtx ctx;

    private static class DefaultTestRunTemplateCollection extends AbstractEtfCollection<TestRunTemplate>
            implements TestRunExecutable {

        // Main collection that contains all ETS (required for resolving dependent ETS)
        private final TrtExecutionContext trtExecutionContext;
        private EtfCollection<TranslationTemplateBundle> translationTemplateBundleCollection;
        private EtsCollection etsCollection;

        private

        DefaultTestRunTemplateCollection(final InstanceCtx ctx, final JSONArray jsonArray,
                final ExecutorService executor,
                final EtsCollection etsCollection,
                final EtfCollection<TranslationTemplateBundle> translationTemplateBundleCollection) {
            super(ctx);
            this.etsCollection = etsCollection;
            this.translationTemplateBundleCollection = translationTemplateBundleCollection;
            this.trtExecutionContext = new TrtExecutionContext(ctx, executor, etsCollection);
            initChildren(jsonArray);
        }

        @Override
        TestRunTemplate doPrepare(final JSONObject jsonObject) {
            return new TestRunTemplateImpl(this.trtExecutionContext, jsonObject, this.etsCollection,
                    this.translationTemplateBundleCollection);
        }

        @Override
        public TestRunCloseable execute(final TestObject testObject, final RunParameters parameters)
                throws RemoteInvocationException, IncompatibleTestObjectTypesException, IllegalStateException {
            return (TestRunCloseable) execute(testObject, null, parameters);
        }

        @Override
        public TestRun execute(final TestObject testObject, final TestRunObserver testRunObserver,
                final RunParameters parameters)
                throws RemoteInvocationException, IncompatibleTestObjectTypesException, IllegalStateException {
            if (this.items.isEmpty()) {
                throw new IllegalStateException("The Executable Test Suite Collection is empty");
            }
            if (this.items.values().size() > 1) {
                throw new IllegalStateException("Starting multiple Test Run Templates is not supported, "
                        + "only one template may be selected.");
            }
            final TestRunTemplate trt = this.items.get(0);
            if (!testObject.baseType().equals(trt.supportedBaseType())) {
                throw new IncompatibleTestObjectTypesException();
            }
            return trtExecutionContext.start(trt, testObject, testRunObserver, parameters);
        }

        @Override
        public RunParameters parameters() {
            return this.etsCollection.parameters();
        }

        EtfCollection<TestRunTemplate> inject(final EtsCollection etsCollection,
                final EtfCollection<TranslationTemplateBundle> translationTemplateBundleCollection) {
            this.translationTemplateBundleCollection = translationTemplateBundleCollection;
            this.etsCollection = etsCollection;
            this.trtExecutionContext.injectExecutableTestSuites(etsCollection);
            return this;
        }
    }

    private final JsonGetRequest apiCall;
    private DefaultTestRunTemplateCollection cachedCollection;

    TestRunTemplateCollectionCmd(final InstanceCtx ctx) {
        this.apiCall = new JsonGetRequest(URI.create(ctx.baseUrl.toString() + PATH), ctx);
        this.ctx = ctx;
    }

    synchronized EtfCollection<TestRunTemplate> query(final CompletableFuture ttCollectionFuture,
            final CompletableFuture etsCollectionFuture) throws RemoteInvocationException {
        final EtfCollection<TranslationTemplateBundle> ttCResult = (EtfCollection<TranslationTemplateBundle>) AbstractCollectionCmd
                .toCollection(ttCollectionFuture);
        final EtsCollection etsResult = (EtsCollection) AbstractCollectionCmd.toCollection(etsCollectionFuture);

        if (apiCall.upToDate()) {
            return cachedCollection.inject(etsResult, ttCResult);
        } else {
            final JSONArray result = apiCall.query().getJSONObject("EtfItemCollection").getJSONObject("testRunTemplates")
                    .getJSONArray("TestRunTemplate");
            cachedCollection = new TestRunTemplateCollectionCmd.DefaultTestRunTemplateCollection(ctx, result, executor,
                    etsResult, ttCResult);
        }
        return cachedCollection;
    }
}
