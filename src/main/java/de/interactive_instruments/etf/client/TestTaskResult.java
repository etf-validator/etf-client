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

import java.util.Collection;
import java.util.Optional;

/**
 * A Test Task Result represents the result of executing exactly one ETS against one Test Object.
 *
 * It is the parent result element of one or multiple {@link TestModuleResult}s and the child of exactly one
 * {@link TestRunResult}.
 *
 * @author Jon Herrmann ( herrmann aT interactive-instruments doT de )
 */
public interface TestTaskResult extends TestResult {

    /**
     * Return the Test Module Results
     *
     * @return TestModuleResults
     */
    Collection<TestModuleResult> testModuleResults();

    /**
     * If internal errors occurred, a container is returned with the error messages, otherwise the container is empty.
     *
     * @return error message or empty
     */
    Optional<Collection<String>> internalErrors();
}
