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
package de.interactive_instruments.etf.client;

import java.net.Authenticator;
import java.net.URL;
import java.text.DecimalFormat;
import java.time.Duration;
import java.util.Locale;

import de.interactive_instruments.etf.client.internal.EndpointBuilderImpl;

/**
 * EtfValidatorClient is used to initialize a connection to an ETF instance and to create {@link EtfEndpoint} objects.
 * The EtfEndpoint objects are thread safe and cache certain responses to ensure a fast connection and avoid unnecessary
 * data exchange.
 *
 * The client using this library should hold the returned EtfEndpoint instance for its own lifetime.
 *
 * @author Jon Herrmann ( herrmann aT interactive-instruments doT de )
 */
public interface EtfValidatorClient {

    /**
     * Create a new builder object for the Endpoint configuration
     *
     * @return builder object
     */
    static EtfValidatorClient create() {
        return new EndpointBuilderImpl();
    }

    /**
     * Set the URL to the validator
     *
     * @param url
     *            URL to use
     * @return builder object
     */
    EtfValidatorClient url(final URL url);

    /**
     * Set the authenticator for the ETF validator
     *
     * @see HttpBasicAuthentication
     *
     * @param authenticator
     *            Authenticator to use for authentication
     * @return builder object
     */
    EtfValidatorClient authenticator(final Authenticator authenticator);

    /**
     * Set the locale so that the messages from the tests are translated into the desired language. This only works if the
     * test developer has provided a language file for that language. English is used as fallback or, if not available, the
     * first language found.
     *
     * @param locale
     *            language for messages
     * @return builder object
     */
    EtfValidatorClient locale(final Locale locale);

    /**
     * Overrides the default timeout for requests, which is 3 minutes. If the response is not received within the specified
     * timeout then an {@link RemoteInvocationException} is thrown.
     *
     * @since 1.1
     *
     * @param duration
     *            the timeout duration
     * @return builder object
     */
    EtfValidatorClient timeout(final Duration duration);

    /**
     * Set the formatting of floating point numbers that occur in messages.
     *
     * Example: <code>new DecimalFormat("0.000", new DecimalFormatSymbols(Locale.ENGLISH))</code>
     *
     * @since 1.6
     *
     * @param format
     *            DecimalFormat for formatting floating point numbers in messages
     * @return builder object
     */
    EtfValidatorClient floatFormat(final DecimalFormat format);

    /**
     * Finalize the Configuration and return an Endpoint object
     *
     * @return new Endpoint object
     */
    EtfEndpoint init();
}
