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
     * @throws EtfIllegalStateException
     *             when the method is invoked on an empty ETS collection or the connection to the remote instance has been
     *             closed
     * @throws TestRunParameterException
     *             if a required Test Run Parameter is not set. Use
     *             {@link #execute(TestObject, TestRunObserver, RunParameters)}
     */
    @Deprecated
    default TestRunCloseable execute(final TestObject testObject)
            throws RemoteInvocationException, IncompatibleTestObjectTypesException, EtfIllegalStateException,
            TestRunParameterException {
        return execute(testObject, (RunParameters) null);
    }

    /**
     * Start a new Test Run.
     *
     * The Client of this API can call the blocking {@link TestRun#result()} } method to wait for the result
     *
     * @param testObject
     *            the Test Object to use
     * @param runParameters
     *            the Parameters for the Test Run
     *
     * @return an object representing the Test Run
     *
     * @throws RemoteInvocationException
     *             if the ETF instance returned an error
     * @throws IncompatibleTestObjectTypesException
     *             when the Test Object Type and the types supported by the ETS are incompatible
     * @throws EtfIllegalStateException
     *             when the method is invoked on an empty ETS collection or the connection to the remote instance has been
     *             closed
     * @throws TestRunParameterException
     *             if a parameter-related error has occurred
     */
    TestRunCloseable execute(final TestObject testObject, final RunParameters runParameters)
            throws RemoteInvocationException, IncompatibleTestObjectTypesException, EtfIllegalStateException,
            TestRunParameterException;

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
     * @throws EtfIllegalStateException
     *             * when the method is invoked on an empty ETS collection or * the connection to the remote instance has
     *             been closed
     * @throws TestRunParameterException
     *             if a required Test Run Parameter is not set. Use
     *             {@link #execute(TestObject, TestRunObserver, RunParameters)}
     */
    @Deprecated
    default TestRun execute(final TestObject testObject, final TestRunObserver testRunObserver)
            throws RemoteInvocationException, IncompatibleTestObjectTypesException, EtfIllegalStateException,
            TestRunParameterException {
        return execute(testObject, testRunObserver, null);
    }

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
     * @param runParameters
     *            the Parameters for the Test Run
     *
     * @return an object representing the Test Run
     *
     * @throws RemoteInvocationException
     *             if the ETF instance returned an error
     * @throws IncompatibleTestObjectTypesException
     *             when the Test Object Type and the types supported by the ETS are incompatible
     * @throws EtfIllegalStateException
     *             when the method is invoked on an empty ETS collection or the connection to the remote instance has been
     *             closed
     * @throws TestRunParameterException
     *             if a parameter-related error occurred
     */
    TestRun execute(final TestObject testObject, final TestRunObserver testRunObserver, final RunParameters runParameters)
            throws RemoteInvocationException, IncompatibleTestObjectTypesException, EtfIllegalStateException,
            TestRunParameterException;

    /**
     * Get applicable Run Parameters
     *
     * @return Test Run Parameters
     */
    RunParameters parameters();
}
