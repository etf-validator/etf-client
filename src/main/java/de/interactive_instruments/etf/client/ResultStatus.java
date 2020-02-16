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
 * Status of ETF items that represent test results.
 *
 * The status code of a result item is based on the aggregation of the statuses of the items at the direct lower level.
 *
 * @see TestTaskResult
 * @see TestModuleResult
 * @see TestCaseResult
 * @see TestStepResult
 * @see TestAssertionResult
 *
 * @author Jon Herrmann ( herrmann aT interactive-instruments doT de )
 */
public enum ResultStatus {

    /**
     *
     * PASSED, if all status values are PASSED
     *
     * ordinal: 0
     */
    PASSED("PASSED"),

    /**
     * FAILED, if at least one status value is FAILED
     *
     * ordinal: 1
     */
    FAILED("FAILED"),

    /**
     * SKIPPED, if at least one status value is SKIPPED because a test case depends on another test case which has the
     * status FAILED or SKIPPED
     *
     * ordinal: 2
     */
    SKIPPED("SKIPPED"),

    /**
     * NOT_APPLICABLE if at least one status value is NOT_APPLICABLE, in the case the test object does not provide the
     * capabilities for executing the test
     *
     * ordinal: 3
     */
    NOT_APPLICABLE("NOT_APPLICABLE"),

    /**
     * INFO, if at least one status value is INFO
     *
     * ordinal: 4
     */
    INFO("INFO"),

    /**
     * WARNING, if at least one status value is WARNING
     *
     * ordinal: 5
     */
    WARNING("WARNING"),

    /**
     * UNDEFINED, in all other cases
     *
     * ordinal: 6
     */
    UNDEFINED("UNDEFINED"),

    /**
     * PASSED_MANUAL, if at least one status value is PASSED_MANUAL (if the test is not automated and the user has to
     * validate results manually based on instructions in the report) and all others are values are PASSED
     *
     * ordinal: 7
     */
    PASSED_MANUAL("PASSED_MANUAL"),

    /**
     * INTERNAL_ERROR, if at least one status value is INTERNAL_ERROR in the case the test engine throws an unexpected error
     * that forces the test run to stop
     *
     * ordinal: 8
     */
    INTERNAL_ERROR("INTERNAL_ERROR"),

    /**
     * Check if the ETF version is newer than the version supported by this library.
     *
     * ordinal: 9
     */
    OTHER("OTHER_UNKNOWN_STATUS_CODE");

    private final String status;

    ResultStatus(final String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return status;
    }

    public static ResultStatus fromString(final String status) {
        for (final ResultStatus s : ResultStatus.values()) {
            if (s.status.equalsIgnoreCase(status)) {
                return s;
            }
        }
        return OTHER;
    }
}
