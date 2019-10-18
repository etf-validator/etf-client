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

import java.net.URI;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
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

        private final ExecutorService executor;
        private EtfCollection<TranslationTemplateBundle> translationTemplateBundleCollection;

        DefaultEtsCollection(final InstanceCtx ctx, final JSONArray jsonArray,
                final ExecutorService executor,
                final EtfCollection<TranslationTemplateBundle> translationTemplateBundleCollection) {
            super(ctx);
            this.executor = executor;
            this.translationTemplateBundleCollection = translationTemplateBundleCollection;
            initChildren(jsonArray);
        }

        DefaultEtsCollection(final InstanceCtx ctx, final Collection<ExecutableTestSuite> executableTestSuites,
                final ExecutorService executor,
                final EtfCollection<TranslationTemplateBundle> translationTemplateBundleCollection) {
            super(ctx, executableTestSuites);
            this.executor = executor;
            this.translationTemplateBundleCollection = translationTemplateBundleCollection;
        }

        @Override
        ExecutableTestSuite doPrepare(final JSONObject jsonObject) {
            return new EtsImpl(this.ctx, jsonObject, this.executor, this.translationTemplateBundleCollection);
        }

        @Override
        public EtsCollection itemsByTag(final Tag tag) {
            final List<ExecutableTestSuite> filteredItems = this.items.values().stream()
                    .filter(it -> it.tagEids().stream().anyMatch(
                            tagEid -> tagEid.equals(tag.eid())))
                    .collect(Collectors.toList());
            return new DefaultEtsCollection(this.ctx, filteredItems, executor, translationTemplateBundleCollection);
        }

        @Override
        public EtsCollection itemsById(final String... eids) {
            if(eids==null || eids.length==0) {
                throw new IllegalArgumentException("EIDs are empty");
            }
            final List<ExecutableTestSuite> filteredItems = Arrays.stream(eids).map(this.items::get).filter(
                    Objects::nonNull).collect(Collectors.toList());
            if(eids.length != filteredItems.size()) {
                for (final String eid : eids) {
                    if(filteredItems.stream().map(ItemMetadata::eid).findFirst().isEmpty()) {
                        throw new IllegalArgumentException("Executable Test Suite with EID '"+eid+"' not found");
                    }
                }
            }
            return new DefaultEtsCollection(this.ctx, filteredItems, executor, translationTemplateBundleCollection);
        }

        @Override
        public TestRun execute(final TestObject testObject)
                throws RemoteInvocationException, IncompatibleTestObjectTypes, IllegalStateException {
            return execute(testObject, null);
        }

        @Override
        public TestRun execute(final TestObject testObject, final TestRunObserver testRunObserver)
                throws RemoteInvocationException, IncompatibleTestObjectTypes, IllegalStateException {
            if (this.items.isEmpty()) {
                throw new IllegalStateException("The Executable Test Suite Collection is empty");
            }
            for (final ExecutableTestSuite ets : this.items.values()) {
                if (!testObject.baseType().equals(ets.supportedBaseType())) {
                    throw new IncompatibleTestObjectTypes();
                }
            }
            return TestRunCmd.prepare(this.ctx, executor, this.items.values(), testObject, testRunObserver);
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
        final Object futureResult;
        try {
            futureResult = ttCollectionFuture.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new IllegalStateException(e);
        }
        if (futureResult instanceof RemoteInvocationException) {
            throw (RemoteInvocationException) futureResult;
        }
        final EtfCollection<TranslationTemplateBundle> ttCResult = (EtfCollection<TranslationTemplateBundle>) futureResult;
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
