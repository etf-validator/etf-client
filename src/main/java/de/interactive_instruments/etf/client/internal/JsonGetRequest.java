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

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.json.JSONException;
import org.json.JSONObject;

import de.interactive_instruments.etf.client.RemoteInvocationException;

/**
 * @author Jon Herrmann ( herrmann aT interactive-instruments doT de )
 */
final class JsonGetRequest extends Request {

    private String ifModifiedSinceValue;

    JsonGetRequest(final URI url, final InstanceCtx ctx) {
        super(url, ctx);
        ifModifiedSinceValue = "Sat, 1 Jan 2000 01:01:01 +0100";
    }

    private HttpRequest.Builder newBuilderWithConditionalRequest() {
        return this.requestBuilder.copy().headers("If-Modified-Since", ifModifiedSinceValue);
    }

    /**
     * Send request with If-Modified-Since header and compare results
     *
     * @return false if remote collection changed, true otherwise
     */
    synchronized boolean upToDate() throws RemoteInvocationException {
        final HttpRequest request = newBuilderWithConditionalRequest().method("HEAD", HttpRequest.BodyPublishers.noBody())
                .build();
        try {
            final HttpResponse<Void> response = httpClient.send(request, HttpResponse.BodyHandlers.discarding());
            checkResponse(response, 200, 204, 304);
            return response.statusCode() == 304;
        } catch (final InterruptedException e) {
            throw new RemoteInvocationException(e);
        } catch (final IOException e) {
            throw new RemoteInvocationException(e);
        }
    }

    synchronized JSONObject query() throws RemoteInvocationException {
        final HttpRequest request = newBuilderWithConditionalRequest().GET().build();
        final HttpResponse.BodyHandler<String> bodyHandler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = null;
        try {
            response = httpClient.send(request, bodyHandler);
            checkResponse(response, 200);
            final String dateHeaderValue = response.headers().firstValue("date").orElse(ifModifiedSinceValue);
            ifModifiedSinceValue = response.headers().firstValue("Last-Modified").orElse(dateHeaderValue);
            return new JSONObject(response.body());
        } catch (final InterruptedException e) {
            throw new RemoteInvocationException(e);
        } catch (final IOException e) {
            throw new RemoteInvocationException(e);
        } catch (final JSONException e) {
            throw new RemoteInvocationException(e, response);
        }
    }

}
