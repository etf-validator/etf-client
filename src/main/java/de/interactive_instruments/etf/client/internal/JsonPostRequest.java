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
import java.math.BigInteger;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

import org.json.JSONException;
import org.json.JSONObject;

import de.interactive_instruments.etf.client.RemoteInvocationException;

/**
 * @author Jon Herrmann ( herrmann aT interactive-instruments doT de )
 */
class JsonPostRequest extends Request {

    private final String boundary = new BigInteger(256, new Random()).toString();

    private static HttpRequest.BodyPublisher ofMimeMultipartData(Map<Object, Object> data,
            String boundary) throws IOException {
        // Original author of this code snippet:
        // https://golb.hplar.ch/2019/01/java-11-http-client.html
        var byteArrays = new ArrayList<byte[]>();
        byte[] separator = ("--" + boundary + "\r\nContent-Disposition: form-data; name=")
                .getBytes(StandardCharsets.UTF_8);
        for (final Map.Entry<Object, Object> entry : data.entrySet()) {
            byteArrays.add(separator);

            if (entry.getValue() instanceof Path) {
                final var path = (Path) entry.getValue();
                final String mimeType = Files.probeContentType(path);
                byteArrays.add(("\"" + entry.getKey() + "\"; filename=\"" + path.getFileName()
                        + "\"\r\nContent-Type: " + mimeType + "\r\n\r\n").getBytes(StandardCharsets.UTF_8));
                byteArrays.add(Files.readAllBytes(path));
                byteArrays.add("\r\n".getBytes(StandardCharsets.UTF_8));
            } else {
                byteArrays.add(("\"" + entry.getKey() + "\"\r\n\r\n" + entry.getValue() + "\r\n")
                        .getBytes(StandardCharsets.UTF_8));
            }
        }
        byteArrays.add(("--" + boundary + "--").getBytes(StandardCharsets.UTF_8));
        return HttpRequest.BodyPublishers.ofByteArrays(byteArrays);
    }

    JsonPostRequest(final URI url, final InstanceCtx ctx) {
        super(url, ctx);
    }

    private HttpRequest.Builder newPostRequest() {
        return this.requestBuilder.copy();
    }

    private JSONObject request(final HttpRequest.BodyPublisher body, final String... headers)
            throws RemoteInvocationException {
        final HttpRequest request = newPostRequest().POST(body).headers(headers).build();
        final HttpResponse.BodyHandler<String> bodyHandler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = null;
        try {
            response = httpClient.send(request, bodyHandler);
            checkResponse(response, 200, 201);
            return new JSONObject(response.body());
        } catch (final InterruptedException e) {
            throw new RemoteInvocationException(e);
        } catch (final IOException e) {
            throw new RemoteInvocationException(e);
        } catch (final JSONException e) {
            throw new RemoteInvocationException(e, response);
        }
    }

    JSONObject post(final JSONObject jsonObject) throws RemoteInvocationException {
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(jsonObject.toString());
        return request(body, "Content-Type", "application/json");
    }

    JSONObject upload(final Path path) throws RemoteInvocationException, IOException {
        final Map<Object, Object> data = new LinkedHashMap<>();
        data.put("fileupload", path);
        final HttpRequest.BodyPublisher body = ofMimeMultipartData(data, boundary);
        return request(body, "Content-Type", "multipart/form-data;boundary=" + boundary);
    }
}
