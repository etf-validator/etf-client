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

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

/**
 * The information about a started test run is updated at regular intervals and the state can be queried using the
 * methods of this interface. Except the other methods, the {@link #result()} method blocks until the Test Run finishes.
 *
 * @author Jon Herrmann ( herrmann aT interactive-instruments doT de )
 */
public interface TestRun extends AutoCloseable {

    /**
     * Cancel the Test Run
     *
     * @throws RemoteInvocationException
     *             if the ETF instance returned an error
     */
    void cancel() throws RemoteInvocationException;

    /**
     * Returns the progress as double in the in the [0.0,1.0] interval.
     *
     * @return in the [0.0,1.0] interval.
     */
    double progress();

    /**
     * Returns true if the Test Run has finished. Non-blocking call.
     *
     * @return false if Test Run has not finished yet, true otherwise
     */
    boolean finished();

    /**
     * The non-blocking call returns the current entries of the log file
     *
     * @return log file entries
     */
    List<String> logEntries();

    /**
     * The blocking call waits until the Test Run has finished and finally returns a Test Run Result.
     *
     * @return the Test Run Result when the Test Run finishes
     *
     * @throws IllegalStateException
     *             if the Test Run has been cancelled
     * @throws ExecutionException
     *             if an exception occurred during the Test Run
     */
    TestRunResult result() throws IllegalStateException, ExecutionException;

    /**
     * Cancels the Test Run on the remote instance
     *
     * @throws RemoteInvocationException
     *             if the ETF instance returned an error
     */
    @Override
    default void close() throws RemoteInvocationException {
        cancel();
    }

    /**
     * A reference to the test run on the remote ETF instance. It serves as
     * an entry point to retrieve additional information and resource of the
     * test run from the ETF instance, like the test report in an HTML format.
     *
     * It must be supplemented with additional parameters and paths that
     * can be found in the API documentation of the ETF instance.
     *
     * @return remote location of this Test Run or <code>empty</code> if the
     * Test Run object is not (yet) initialized
     */
    Optional<URI> remoteRef();
}
