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
 * The information about a started test run is updated at regular intervals and the state can be queried using the
 * methods of this interface. Except the other methods, the {@link #result()} method blocks until the Test Run finishes.
 *
 * @author Jon Herrmann ( herrmann aT interactive-instruments doT de )
 */
public interface TestRunCloseable extends TestRun, AutoCloseable {

    /**
     * Cancels the Test Run on the remote instance
     *
     * Please note: if the Test Run has already finished, this will delete the Test Report from the remote instance!
     *
     * Using it in a try-with-resources statement only makes sense if the test results are retrieved and processed within
     * the statement. After exiting the statement, the report is deleted!
     *
     * @throws RemoteInvocationException
     *             if the ETF instance returned an error
     */
    @Override
    default void close() throws RemoteInvocationException {
        cancel();
    }
}
