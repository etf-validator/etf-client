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
package de.interactive_instruments.etf.client.internal;

import java.io.IOException;
import java.net.Authenticator;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;

import org.json.JSONObject;

import de.interactive_instruments.etf.client.AdHocTestObject;
import de.interactive_instruments.etf.client.AdHocTestObjectFactory;
import de.interactive_instruments.etf.client.RemoteInvocationException;
import de.interactive_instruments.etf.client.TestObjectBaseType;

/**
 * Factory for creating AdHoc Test Objects
 *
 * @author Jon Herrmann ( herrmann aT interactive-instruments doT de )
 */
final class AdHocTestObjectFactoryImpl implements AdHocTestObjectFactory {

    final static String PATH = "/TestObjects?action=upload";

    private final JsonPostRequest uploadRequest;

    AdHocTestObjectFactoryImpl(final InstanceCtx ctx) {
        this.uploadRequest = new JsonPostRequest(URI.create(ctx.baseUrl.toString() + PATH), ctx);
    }

    public AdHocTestObject fromDataSet(final Path pathToDataSet)
            throws RemoteInvocationException, IOException {
        final JSONObject jsonResponse = this.uploadRequest.upload(pathToDataSet);
        return AdHocTestObjectImpl.create(jsonResponse);
    }

    public AdHocTestObject fromDataSet(final URL url, final Authenticator authenticator) {
        return AdHocTestObjectImpl.create(url, authenticator, TestObjectBaseType.DATA_SET);
    }

    public AdHocTestObject fromService(final URL url, final Authenticator authenticator) {
        return AdHocTestObjectImpl.create(url, authenticator, TestObjectBaseType.SERVICE);
    }
}
