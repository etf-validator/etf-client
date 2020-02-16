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
import java.util.function.Consumer;

/**
 * A Test Module Result is the parent result element of zero or multiple {@link TestCaseResult}s and the child of
 * exactly one {@link TestTaskResult}.
 *
 * Please note: If the label of this element is set to the value "IGNORE", it will be ignored during the traversal of
 * {@link TestResult}s.
 *
 * @see TestResult#forEach(Consumer)
 *
 * @author Jon Herrmann ( herrmann aT interactive-instruments doT de )
 */
public interface TestModuleResult extends TestResult {

    /**
     * Return the Test Case Results
     *
     * @return TestCaseResult
     */
    Collection<TestCaseResult> testCaseResults();
}
