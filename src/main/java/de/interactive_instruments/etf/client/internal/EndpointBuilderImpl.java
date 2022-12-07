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
import java.net.URL;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.Duration;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.ExecutorService;

import de.interactive_instruments.etf.client.EtfEndpoint;
import de.interactive_instruments.etf.client.EtfValidatorClient;
import de.interactive_instruments.etf.client.EtfValidatorClientWithExecutorService;

/**
 * @author Jon Herrmann ( herrmann aT interactive-instruments doT de )
 */
final public class EndpointBuilderImpl implements EtfValidatorClientWithExecutorService {

    private URL url;
    private Locale locale = Locale.getDefault();
    private Authenticator auth;
    private Duration timeout = Duration.ofMinutes(5);
    private DecimalFormat floatFormat = new DecimalFormat(
            "0.#######", new DecimalFormatSymbols(Locale.ENGLISH));
    private ExecutorService executorService = null;
    private Duration retryDelay = Duration.ofSeconds(0);
    private int retryAttempts = 0;

    @Override
    public EtfValidatorClient url(final URL url) {
        this.url = url;
        return this;
    }

    @Override
    public EtfValidatorClient authenticator(final Authenticator authenticator) {
        this.auth = authenticator;
        return this;
    }

    @Override
    public EtfValidatorClient locale(final Locale locale) {
        this.locale = locale;
        return this;
    }

    @Override
    public EtfValidatorClient timeout(final Duration timeout) {
        this.timeout = timeout;
        return this;
    }

    @Override
    public EtfValidatorClient floatFormat(final DecimalFormat floatFormat) {
        this.floatFormat = floatFormat;
        return this;
    }

    @Override
    public EtfValidatorClientWithExecutorService executorService(final ExecutorService executorService) {
        this.executorService = executorService;
        return this;
    }

    @Override
    public EtfEndpoint init() {
        Objects.requireNonNull(this.url, "URL not set");
        Objects.requireNonNull(this.locale, "Locale not set");
        return new EndpointImpl(executorService, this.url, this.locale, this.auth, this.timeout,
                this.floatFormat, this.retryDelay, this.retryAttempts);
    }

    @Override
    public EtfValidatorClient retryOnConnectionReset(Duration delay, int attempts) {
        this.retryDelay = delay;
        this.retryAttempts = attempts;
        return this;
    }
}
