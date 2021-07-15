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

import java.net.Authenticator;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.DecimalFormat;
import java.time.Duration;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.interactive_instruments.etf.client.*;

/**
 * @author Jon Herrmann ( herrmann aT interactive-instruments doT de )
 */
final class EndpointImpl implements EtfEndpoint {

    private final static String apiVersion = "v2";

    private final InstanceCtx ctx;
    private final InstanceStatusCmd statusCmd;
    private final TagCollectionCmd tagCmd;
    private final EtsCollectionCmd etsCollectionCmd;
    private final TestRunTemplateCollectionCmd trtCollectionCmd;
    private final TranslationTemplateBundleCollectionCmd ttCollectionCmd;
    private final AdHocTestObjectFactoryImpl adHocTestObjectFactory;
    private boolean testRunTemplatesSupported;

    EndpointImpl(final URL baseUrl, final Locale locale, final Authenticator auth, final Duration timout,
            final DecimalFormat floatFormat) {
        this.ctx = new InstanceCtx(toBaseUri(baseUrl), auth, locale, timout, floatFormat);
        this.statusCmd = new InstanceStatusCmd(ctx);
        this.tagCmd = new TagCollectionCmd(ctx);
        this.etsCollectionCmd = new EtsCollectionCmd(ctx);
        this.trtCollectionCmd = new TestRunTemplateCollectionCmd(ctx);
        this.ttCollectionCmd = new TranslationTemplateBundleCollectionCmd(ctx);
        this.adHocTestObjectFactory = new AdHocTestObjectFactoryImpl(ctx);
    }

    private static URI toBaseUri(final URL baseUrl) {
        try {
            final String url = baseUrl.toURI().toString();
            if (url.endsWith(apiVersion)) {
                return baseUrl.toURI();
            } else if (url.endsWith("/")) {
                return new URI(url + apiVersion);
            }
            return new URI(url + "/" + apiVersion);
        } catch (final URISyntaxException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private void ensureTestRunTemplateSupport() throws RemoteInvocationException {
        if (!testRunTemplatesSupported) {
            final EtfStatus status = status();
            final String version = status.version();
            try {
                final Pattern p = Pattern.compile("(\\d+)\\.(\\d+)\\..*");
                final Matcher m = p.matcher(version);
                if (m.matches()) {
                    final int major = Integer.parseInt(m.group(1));
                    final int minor = Integer.parseInt(m.group(2));
                    if (major >= 2 && minor >= 1) {
                        testRunTemplatesSupported = true;
                        return;
                    }
                }
            } catch (final NumberFormatException e) {
                // ignore
            }
            throw new FeatureNotSupportedException(version, "2.1");
        }
    }

    @Override
    public EtfStatus status() throws RemoteInvocationException {
        return statusCmd.query();
    }

    @Override
    public boolean available() {
        try {
            status();
            return true;
        } catch (RemoteInvocationException e) {
            return false;
        }
    }

    @Override
    public EtsCollection executableTestSuites() throws RemoteInvocationException {
        final CompletableFuture<?> ttCollectionFuture = CompletableFuture.supplyAsync(() -> {
            try {
                return ttCollectionCmd.query();
            } catch (final RemoteInvocationException e) {
                return e;
            }
        });
        return this.etsCollectionCmd.query(ttCollectionFuture);
    }

    @Override
    public EtfCollection<Tag> tags() throws RemoteInvocationException {
        return tagCmd.query();
    }

    @Override
    public EtfCollection<TestRunTemplate> testRunTemplates() throws RemoteInvocationException {
        ensureTestRunTemplateSupport();
        final CompletableFuture ttCollectionFuture = CompletableFuture.supplyAsync(() -> {
            try {
                return ttCollectionCmd.query();
            } catch (final RemoteInvocationException e) {
                return e;
            }
        });
        final CompletableFuture etsCollectionFuture = CompletableFuture.supplyAsync(() -> {
            try {
                return etsCollectionCmd.query(ttCollectionFuture);
            } catch (final RemoteInvocationException e) {
                return e;
            }
        });
        return this.trtCollectionCmd.query(ttCollectionFuture, etsCollectionFuture);
    }

    @Override
    public AdHocTestObjectFactory newAdHocTestObject() {
        return adHocTestObjectFactory;
    }

    @Override
    public String sessionId() {
        return this.ctx.sessionId;
    }

    @Override
    public void close() {
        this.ctx.close();
    }
}
