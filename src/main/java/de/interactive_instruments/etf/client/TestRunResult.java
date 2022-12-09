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

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

/**
 * A Test Run Result represents the execution of one or multiple ETS against one Test Object.
 *
 * It is the root result element of one or multiple {@link TestTaskResult}s. If a TestRunResult is used for iteration,
 * the iterator will also traverse all sub results starting with the first Test Task Result.
 *
 * @author Jon Herrmann ( herrmann aT interactive-instruments doT de )
 */
public interface TestRunResult extends Iterable<TestResult> {

    /**
     * Get the log entries that were logged during the Test Run
     *
     * @return start as date
     *
     * @throws RemoteInvocationException
     *             reading the log file failed
     */
    List<String> logEntries() throws RemoteInvocationException;

    /**
     * The start data
     *
     * @return start as date
     */
    LocalDateTime startDate();

    /**
     * The duration of this test in milliseconds
     *
     * @return duration in milliseconds
     */
    long duration();

    Collection<TestTaskResult> testTaskResults();

    /**
     * Delete the report from the remote ETF instance.
     *
     * @throws RemoteInvocationException
     *             if the ETF instance returned an error
     */
    void delete() throws RemoteInvocationException;
}
