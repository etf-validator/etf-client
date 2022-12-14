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

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.json.JSONException;

import de.interactive_instruments.etf.client.RemoteInvocationException;

/**
 * @author Jon Herrmann ( herrmann aT interactive-instruments doT de )
 */
class SimpleGetRequest extends Request {

    SimpleGetRequest(final URI url, final InstanceCtx ctx) {
        super(url, ctx);
    }

    synchronized String query() throws RemoteInvocationException {
        final HttpRequest request = this.requestBuilder.copy().GET().build();
        final HttpResponse.BodyHandler<String> bodyHandler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = null;
        try {
            response = httpClient.send(request, bodyHandler);
            checkResponse(response, 200);
            final String body = response.body();
            return body != null ? body : "";
        } catch (final InterruptedException e) {
            throw new RemoteInvocationException(e);
        } catch (final IOException e) {
            throw new RemoteInvocationException(e);
        } catch (final JSONException e) {
            throw new RemoteInvocationException(e, response);
        }
    }

}
