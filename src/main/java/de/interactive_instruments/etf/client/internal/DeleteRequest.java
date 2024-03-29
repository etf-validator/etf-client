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

import static java.net.http.HttpRequest.BodyPublishers.noBody;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import de.interactive_instruments.etf.client.RemoteInvocationException;

/**
 * @author Jon Herrmann ( herrmann aT interactive-instruments doT de )
 */
class DeleteRequest extends Request {

    DeleteRequest(final URI url, final InstanceCtx ctx) {
        super(url, ctx);
    }

    void delete() throws RemoteInvocationException {
        final HttpRequest request = this.requestBuilder.method("DELETE", noBody()).build();
        int attempts = retryAttempts;
        while (true) {
            try {
                final HttpResponse<Void> response = httpClient.send(request, HttpResponse.BodyHandlers.discarding());
                checkResponse(response, 204);
                break;
            } catch (final InterruptedException e) {
                throw new RemoteInvocationException(e);
            } catch (final IOException e) {
                if (attempts-- == 0) throw new RemoteInvocationException(e);
                delay();
            }
        }
    }
}
