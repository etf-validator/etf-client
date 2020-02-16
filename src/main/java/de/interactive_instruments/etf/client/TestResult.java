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

import java.time.LocalDateTime;

/**
 * The Test Result of an test item
 *
 * If a Test Result item is used for iteration, the iterator will also traverse the sub results.
 *
 * @author Jon Herrmann ( herrmann aT interactive-instruments doT de )
 */
public interface TestResult extends Iterable<TestResult> {

    /**
     * Returns the string representation of this type
     *
     * @return type as string
     */
    String type();

    /**
     * Label of the associated Test in the ETS
     *
     * @return label as string
     */
    String label();

    /**
     * Description of the associated Test in the ETS
     *
     * @return label as string
     */
    String description();

    /**
     * The status that has been aggregated from the child tests items
     *
     * @return label as string
     */
    ResultStatus resultStatus();

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
}
