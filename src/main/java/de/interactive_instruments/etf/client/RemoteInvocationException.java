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
package de.interactive_instruments.etf.client;

import java.io.IOException;
import java.net.http.HttpResponse;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * An error returned by the ETF instance
 *
 * @author Jon Herrmann ( herrmann aT interactive-instruments doT de )
 */
public class RemoteInvocationException extends Exception {

    private final int statusCode;
    private final Object responseBody;

    public RemoteInvocationException(final HttpResponse response) {
        this.statusCode = response.statusCode();
        JSONObject jsonObject = null;
        if (response.body() != null)
            try {
                jsonObject = new JSONObject(response.body().toString());
            } catch (JSONException ignored) {}
        if (jsonObject != null) {
            this.responseBody = jsonObject;
        } else {
            this.responseBody = response.body();
        }
    }

    public RemoteInvocationException(final JSONException jsonException, final HttpResponse response) {
        super(jsonException);
        if (response != null) {
            this.statusCode = response.statusCode();
            this.responseBody = response.body();
        } else {
            this.statusCode = 0;
            this.responseBody = null;
        }
    }

    public RemoteInvocationException(final IOException e) {
        super(e);
        this.statusCode = 0;
        this.responseBody = null;
    }

    public RemoteInvocationException(final InterruptedException e) {
        super(e);
        this.statusCode = 0;
        this.responseBody = null;
    }

    public int statusCode() {
        return this.statusCode;
    }

    public Object responseBody() {
        return this.responseBody;
    }

    public String toString() {
        if (this.statusCode == 0) {
            return super.toString();
        }
        if (this.responseBody != null && this.responseBody instanceof JSONObject) {
            final JSONObject jsonObject = ((JSONObject) this.responseBody);
            if (jsonObject.has("error")) {
                return this.statusCode + " - " + jsonObject.getString("error");
            }
        }
        return String.valueOf(this.statusCode);
    }
}
