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

import java.io.IOException;
import java.net.Authenticator;
import java.net.URL;
import java.nio.file.Path;

/**
 * Factory for creating AdHoc Test Objects
 *
 * @author Jon Herrmann ( herrmann aT interactive-instruments doT de )
 */
public interface AdHocTestObjectFactory {

    /**
     * Create a temporary Test Object for a Data Set that is represented by a (local) path.
     *
     * @param pathToDataSet
     *            a path to the Data Set
     * @return a temporary Test Object
     * @throws RemoteInvocationException
     *             if the ETF instance returned an error
     * @throws IOException
     *             if reading the data set from the path failed
     */
    AdHocTestObject fromDataSet(final Path pathToDataSet) throws RemoteInvocationException, IOException;

    /**
     * Create a temporary Test Object for a Data Set that is represented by a remote URL.
     *
     * @see HttpBasicAuthentication
     *
     * @param url
     *            an URL to the Service
     *
     * @return a temporary Test Object
     */
    default AdHocTestObject fromDataSet(final URL url) {
        return fromDataSet(url, null);
    }

    /**
     * Create a temporary Test Object for a Data Set that is represented by a remote URL.
     *
     * @see HttpBasicAuthentication
     *
     * @param url
     *            an URL to the Service
     * @param authenticator
     *            Authenticator to use for authentication
     *
     * @return a temporary Test Object
     */
    AdHocTestObject fromDataSet(final URL url, final Authenticator authenticator);

    /**
     * Create a temporary Test Object for a Service
     *
     * @see HttpBasicAuthentication
     *
     * @param url
     *            an URL to the Service
     * @param authenticator
     *            Authenticator to use for authentication
     *
     * @return a temporary Test Object
     */
    AdHocTestObject fromService(final URL url, final Authenticator authenticator);

    /**
     * Create a temporary Test Object for a Service
     *
     * @see HttpBasicAuthentication
     *
     * @param url
     *            an URL to the Service
     *
     * @return a temporary Test Object
     */
    default AdHocTestObject fromService(final URL url) {
        return fromService(url, null);
    }

}
