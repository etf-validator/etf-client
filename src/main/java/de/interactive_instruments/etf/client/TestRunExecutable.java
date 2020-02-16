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
 * Represents objects that can execute Test Runs
 *
 * @author Jon Herrmann ( herrmann aT interactive-instruments doT de )
 */
public interface TestRunExecutable {

    /**
     * Start a new Test Run.
     *
     * The Client of this API can call the blocking {@link TestRun#result()} } method to wait for the result
     *
     * @param testObject
     *            the Test Object to use
     *
     * @return an object representing the Test Run
     *
     * @throws RemoteInvocationException
     *             if the ETF instance returned an error
     * @throws IncompatibleTestObjectTypesException
     *             when the Test Object Type and the types supported by the ETS are incompatible
     * @throws IllegalStateException
     *             when the method is invoked on an empty ETS collection
     */
    TestRun execute(final TestObject testObject)
            throws RemoteInvocationException, IncompatibleTestObjectTypesException, IllegalStateException;

    /**
     * Start a new Test Run.
     *
     * The Client of the API can implement the {@link TestRunObserver} interface and pass a object that will be called when
     * the Test Run has finished.
     *
     * @param testObject
     *            the Test Object to use
     * @param testRunObserver
     *            an Object that implements a callback interface
     *
     * @return an object representing the Test Run
     *
     * @throws RemoteInvocationException
     *             if the ETF instance returned an error
     * @throws IncompatibleTestObjectTypesException
     *             when the Test Object Type and the types supported by the ETS are incompatible
     * @throws IllegalStateException
     *             when the method is invoked on an empty ETS collection
     */
    TestRun execute(final TestObject testObject, final TestRunObserver testRunObserver)
            throws RemoteInvocationException, IncompatibleTestObjectTypesException, IllegalStateException;
}
