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
package de.interactive_instruments.etf.client;

/**
 * An interface to communicate with a remote ETF instance. There is no permanent connection to the instance, retrieved
 * information are cached.
 *
 * With each operation on the API it is checked whether information is outdated and if necessary automatically renewed.
 *
 * @author Jon Herrmann ( herrmann aT interactive-instruments doT de )
 */
public interface EtfEndpoint extends AutoCloseable {

    /**
     * Returns true if the endpoint is reachable.
     *
     * @return true if endpoint reachable, false otherwise
     */
    boolean available();

    /**
     * Get a collection of Executable Test Suites that are installed on the remote instance.
     *
     * @return collection of Executable Test Suites
     * @throws RemoteInvocationException
     *             if the ETF instance returned an error
     */
    EtsCollection executableTestSuites() throws RemoteInvocationException;

    /**
     * Get a collection of Tags that are assigned to the Executable Test Suites
     *
     * @return collection of Tags
     * @throws RemoteInvocationException
     *             if the ETF instance returned an error
     */
    EtfCollection<Tag> tags() throws RemoteInvocationException;

    /**
     * Get a collection of Test Run Templates that are installed on the remote instance.
     *
     * @since 1.1
     *
     * @return collection of Test Run Templates
     * @throws RemoteInvocationException
     *             if the ETF instance returned an error
     */
    EtfCollection<TestRunTemplate> testRunTemplates() throws RemoteInvocationException;

    /**
     * Return a factory for creating AdHoc Test Objects
     *
     * @return AdHocTestObjectFactory for creating temporary Test Objects
     */
    AdHocTestObjectFactory newAdHocTestObject();

    /**
     * Get information about workload and health information.
     *
     * @return status object
     * @throws RemoteInvocationException
     *             if the ETF instance returned an error
     */
    EtfStatus status() throws RemoteInvocationException;

    /**
     * Get the currently used session ID.
     *
     * @return session UUID as string
     */
    String sessionId();

    /**
     * Explicitly releases internal resources.
     *
     * Running Tests will be canceled.
     */
    @Override
    void close();
}
