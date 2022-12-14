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

import java.util.Collections;
import java.util.Iterator;

/**
 * A Test Assertion Result does not possess any child result elements. It is the child of exactly one
 * {@link TestStepResult}.
 *
 * @author Jon Herrmann ( herrmann aT interactive-instruments doT de )
 */
public interface TestAssertionResult extends TestResultMessageHolder, TestResult {

    /**
     * Returns an iterator that has no elements.
     *
     * This item does not possess any child results.
     *
     * @return an iterator that has no elements
     */
    @Override
    default Iterator<TestResult> iterator() {
        return Collections.emptyIterator();
    }
}
