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
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONObject;

import de.interactive_instruments.etf.client.*;

/**
 * @author Jon Herrmann ( herrmann aT interactive-instruments doT de )
 */
class EtsCollectionCmd {

    private final static String PATH = "/ExecutableTestSuites";
    private final ExecutorService executor = Executors.newCachedThreadPool();
    private final InstanceCtx ctx;

    private static class DefaultEtsCollection extends AbstractEtfCollection<ExecutableTestSuite> implements EtsCollection {

        // Main collection that contains all ETS (required for resolving dependent ETS)
        private final EtsExecutionContext etsExecutionContext;
        private EtfCollection<TranslationTemplateBundle> translationTemplateBundleCollection;
        private final RunParameters runParameters;

        DefaultEtsCollection(final InstanceCtx ctx, final JSONArray jsonArray,
                final ExecutorService executor,
                final EtfCollection<TranslationTemplateBundle> translationTemplateBundleCollection) {
            super(ctx);
            this.translationTemplateBundleCollection = translationTemplateBundleCollection;
            this.etsExecutionContext = new EtsExecutionContext(ctx, executor);
            initChildren(jsonArray);
            this.etsExecutionContext.injectExecutableTestSuites(this.items.values());
            this.runParameters = mergeRunParameters(this.items.values());
        }

        private DefaultEtsCollection(final EtsExecutionContext etsExecutionContext,
                final Collection<ExecutableTestSuite> filteredItems,
                final EtfCollection<TranslationTemplateBundle> translationTemplateBundleCollection) {
            super(etsExecutionContext.instanceCtx, filteredItems);
            this.etsExecutionContext = etsExecutionContext;
            this.translationTemplateBundleCollection = translationTemplateBundleCollection;
            this.runParameters = mergeRunParameters(filteredItems);
        }

        private static RunParameters mergeRunParameters(final Iterable<ExecutableTestSuite> etss) {
            final Map<String, String> allParameters = new HashMap<>();
            for (final ExecutableTestSuite ets : etss) {
                allParameters.putAll(ets.parameters().map());
            }
            return RunParametersImpl.init(allParameters);
        }

        @Override
        ExecutableTestSuite doPrepare(final JSONObject jsonObject) {
            return new ExecutableTestSuiteImpl(this.etsExecutionContext, jsonObject, this.translationTemplateBundleCollection);
        }

        @Override
        public EtsCollection itemsByTag(final Tag tag) {
            final List<ExecutableTestSuite> filteredItems = this.items.values().stream()
                    .filter(it -> it.tagEids().stream().anyMatch(
                            tagEid -> tagEid.equals(tag.eid())))
                    .collect(Collectors.toList());
            return new DefaultEtsCollection(this.etsExecutionContext, filteredItems, translationTemplateBundleCollection);
        }

        @Override
        public EtsCollection itemsById(final String... eids) {
            if (eids == null || eids.length == 0) {
                throw new IllegalArgumentException("EIDs are empty");
            }
            final List<ExecutableTestSuite> filteredItems = Arrays.stream(eids).map(this.items::get).filter(
                    Objects::nonNull).collect(Collectors.toList());
            if (eids.length != filteredItems.size()) {
                for (final String eid : eids) {
                    if (filteredItems.stream().map(ItemMetadata::eid).findFirst().isEmpty()) {
                        throw new IllegalArgumentException("Executable Test Suite with EID '" + eid + "' not found");
                    }
                }
            }
            return new DefaultEtsCollection(this.etsExecutionContext, filteredItems, translationTemplateBundleCollection);
        }

        @Override
        public TestRun execute(final TestObject testObject, final RunParameters parameters)
                throws RemoteInvocationException, IncompatibleTestObjectTypesException, IllegalStateException {
            return execute(testObject, null, parameters);
        }

        @Override
        public TestRun execute(final TestObject testObject, final TestRunObserver testRunObserver,
                final RunParameters parameters)
                throws RemoteInvocationException, IncompatibleTestObjectTypesException, IllegalStateException {
            if (this.items.isEmpty()) {
                throw new IllegalStateException("The Executable Test Suite Collection is empty");
            }
            for (final ExecutableTestSuite ets : this.items.values()) {
                if (!testObject.baseType().equals(ets.supportedBaseType())) {
                    throw new IncompatibleTestObjectTypesException();
                }
            }
            return etsExecutionContext.start(this.items.values(), testObject, testRunObserver, parameters);
        }

        @Override
        public RunParameters parameters() {
            return this.runParameters;
        }

        EtsCollection inject(final EtfCollection<TranslationTemplateBundle> translationTemplateBundleCollection) {
            this.translationTemplateBundleCollection = translationTemplateBundleCollection;
            return this;
        }
    }

    private final JsonGetRequest apiCall;
    private DefaultEtsCollection cachedCollection;

    EtsCollectionCmd(final InstanceCtx ctx) {
        this.apiCall = new JsonGetRequest(URI.create(ctx.baseUrl.toString() + PATH), ctx);
        this.ctx = ctx;
    }

    synchronized EtsCollection query(final CompletableFuture ttCollectionFuture) throws RemoteInvocationException {
        final EtfCollection<TranslationTemplateBundle> ttCResult = (EtfCollection<TranslationTemplateBundle>) AbstractCollectionCmd
                .toCollection(ttCollectionFuture);
        if (apiCall.upToDate()) {
            return cachedCollection.inject(ttCResult);
        } else {
            final JSONArray result = apiCall.query().getJSONObject("EtfItemCollection").getJSONObject("executableTestSuites")
                    .getJSONArray("ExecutableTestSuite");
            cachedCollection = new DefaultEtsCollection(ctx, result, executor, ttCResult);
        }
        return cachedCollection;
    }
}
