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
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.interactive_instruments.etf.client.RemoteInvocationException;

/**
 * @author Jon Herrmann ( herrmann aT interactive-instruments doT de )
 */
abstract class Request {

    private final static String USER_AGENT_HEADER = "ETF Client 1.6";
    private final static String ACCEPT_HEADER = "application/json";
    protected final Logger logger = LoggerFactory.getLogger(Request.class);

    final HttpRequest.Builder requestBuilder;
    final HttpClient httpClient;

    Request(final URI url, final InstanceCtx ctx) {
        this.requestBuilder = HttpRequest.newBuilder(url)
                .version(HttpClient.Version.HTTP_1_1)
                .timeout(ctx.timeout)
                .header("Accept", ACCEPT_HEADER)
                .header("Accept-Language", ctx.locale.getLanguage())
                .header("User-Agent", USER_AGENT_HEADER)
                .header("ETF-Client-Session-ID", ctx.sessionId);

        final HttpClient.Builder clientBuilder = HttpClient.newBuilder()
                .executor(ctx.executor())
                .version(HttpClient.Version.HTTP_1_1)
                .followRedirects(HttpClient.Redirect.NORMAL);
        if (ctx.auth != null) {
            this.httpClient = clientBuilder.authenticator(ctx.auth).build();
        } else {
            this.httpClient = clientBuilder.build();
        }
    }

    final void checkResponse(final HttpResponse response, final int... expectedCodes) throws RemoteInvocationException {
        for (final int code : expectedCodes) {
            if (response.statusCode() == code) {
                return;
            }
        }
        throw new RemoteInvocationException(response);
    }
}
