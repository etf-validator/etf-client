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

import org.json.JSONObject;

import de.interactive_instruments.etf.client.AdHocTestObject;
import de.interactive_instruments.etf.client.TestObjectBaseType;

/**
 * @author Jon Herrmann ( herrmann aT interactive-instruments doT de )
 */
final class AdHocTestObjectImpl implements AdHocTestObject {

    private final TestObjectBaseType baseType;
    private final JSONObject preparedJson;

    private AdHocTestObjectImpl(final JSONObject testObjectUploadResponse) {
        this.baseType = TestObjectBaseType.DATA_SET;
        final String idRef = testObjectUploadResponse.getJSONObject("testObject").getString("id");
        preparedJson = new JSONObjectWithOrderedAttributes();
        preparedJson.put("id", idRef);
    }

    private AdHocTestObjectImpl(final URL url, final Authenticator authenticator,
            final TestObjectBaseType baseType) {
        this.baseType = baseType;
        preparedJson = new JSONObjectWithOrderedAttributes();
        final JSONObject reference = new JSONObjectWithOrderedAttributes();
        if (baseType.equals(TestObjectBaseType.SERVICE)) {
            reference.put("serviceEndpoint", url);
        } else if (baseType.equals(TestObjectBaseType.DATA_SET)) {
            reference.put("data", url);
        }
        preparedJson.put("resources", reference);
        if (authenticator != null) {
            if (authenticator instanceof HttpBasicAuthenticationImpl) {
                preparedJson.putOnce("username", ((HttpBasicAuthenticationImpl) authenticator).username());
                preparedJson.putOnce("password", ((HttpBasicAuthenticationImpl) authenticator).password());
            } else {
                throw new IllegalArgumentException("Authenticator not supported: " + authenticator.getClass().getName());
            }
        }
    }

    static AdHocTestObjectImpl create(final URL serviceUrl, final Authenticator authenticator,
            final TestObjectBaseType baseType) {
        return new AdHocTestObjectImpl(serviceUrl, authenticator, baseType);
    }

    static AdHocTestObjectImpl create(final JSONObject testObjectUploadResponse) {
        return new AdHocTestObjectImpl(testObjectUploadResponse);
    }

    @Override
    public TestObjectBaseType baseType() {
        return this.baseType;
    }

    JSONObject toJson() {
        return this.preparedJson;
    }

    @Override
    public String toString() {
        return toJson().toString();
    }

    @Override
    public String eid() {
        if (preparedJson.has("id")) {
            return preparedJson.getString("id");
        } else {
            throw new UnsupportedOperationException("A remotely referenced Test Object does not possess an EID");
        }
    }
}
