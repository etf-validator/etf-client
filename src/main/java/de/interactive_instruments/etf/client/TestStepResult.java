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

import java.util.Collection;
import java.util.Map;
import java.util.function.Consumer;

/**
 * A Test Step is the parent result element of zero or multiple {@link TestAssertionResult}s and the child of exactly
 * one {@link TestCaseResult}.
 *
 * Please note: If the label of this element is set to the value "IGNORE", it will be ignored during the traversal of
 * {@link TestResult}s.
 *
 * @see TestResult#forEach(Consumer)
 *
 * @author Jon Herrmann ( herrmann aT interactive-instruments doT de )
 */
public interface TestStepResult extends TestResultMessageHolder, TestResult {

    /**
     * Additional information that are attached to this result.
     *
     * @return a map with the name of the attachment as key and the additional information as value
     */
    Map<String, String> getAttachment();

    /**
     * Return assertion results
     *
     * @return assertion results or null
     */
    Collection<TestAssertionResult> testAssertionResults();
}
