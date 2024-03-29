/**
 * Copyright 2019-2022 interactive instruments GmbH
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
import java.util.stream.Collectors;

import org.json.JSONObject;

import de.interactive_instruments.etf.client.*;

/**
 * @author Jon Herrmann ( herrmann aT interactive-instruments doT de )
 */
class EtsCollectionCmd {

    private final static String PATH = "/ExecutableTestSuites";
    private final InstanceCtx ctx;

    private static class DefaultEtsCollection extends AbstractEtfCollection<ExecutableTestSuite> implements EtsCollection {

        // Main collection that contains all ETS (required for resolving dependent ETS)
        private final EtsExecutionContext etsExecutionContext;
        private EtfCollection<TranslationTemplateBundle> translationTemplateBundleCollection;
        private final RunParameters runParameters;

        DefaultEtsCollection(final InstanceCtx ctx, final Collection<JSONObject> jsonObjects,
                final ExecutorService executor,
                final EtfCollection<TranslationTemplateBundle> translationTemplateBundleCollection) {
            super(ctx);
            this.translationTemplateBundleCollection = translationTemplateBundleCollection;
            this.etsExecutionContext = new EtsExecutionContext(ctx, executor);
            initChildren(jsonObjects);
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
                throw new EtfIllegalArgumentException("EIDs are empty");
            }
            final List<ExecutableTestSuite> filteredItems = Arrays.stream(eids).map(this.items::get).filter(
                    Objects::nonNull).collect(Collectors.toList());
            if (eids.length != filteredItems.size()) {
                for (final String eid : eids) {
                    if (filteredItems.stream().map(ItemMetadata::eid).findFirst().isEmpty()) {
                        throw new EtfIllegalArgumentException("Executable Test Suite with EID '" + eid + "' not found");
                    }
                }
            }
            return new DefaultEtsCollection(this.etsExecutionContext, filteredItems, translationTemplateBundleCollection);
        }

        @Override
        public TestRunCloseable execute(final TestObject testObject, final RunParameters parameters)
                throws RemoteInvocationException, IncompatibleTestObjectTypesException, EtfIllegalStateException {
            return (TestRunCloseable) execute(testObject, null, parameters);
        }

        @Override
        public TestRun execute(final TestObject testObject, final TestRunObserver testRunObserver,
                final RunParameters parameters)
                throws RemoteInvocationException, IncompatibleTestObjectTypesException, EtfIllegalStateException {
            if (this.items.isEmpty()) {
                throw new EtfIllegalStateException("The Executable Test Suite Collection is empty");
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

    static RunParameters mergeRunParameters(final Iterable<ExecutableTestSuite> etss) {
        final Collection<RunParameters> allParameters = new ArrayList<>();
        for (final ExecutableTestSuite ets : etss) {
            allParameters.add(ets.parameters());
        }
        return RunParametersImpl.merge(allParameters);
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
            final Collection<JSONObject> result = new JSONObjectOrArray(apiCall.query().getJSONObject("EtfItemCollection")
                    .getJSONObject("executableTestSuites")).get("ExecutableTestSuite");
            cachedCollection = new DefaultEtsCollection(ctx, result, ctx.executor(), ttCResult);
        }
        return cachedCollection;
    }
}
