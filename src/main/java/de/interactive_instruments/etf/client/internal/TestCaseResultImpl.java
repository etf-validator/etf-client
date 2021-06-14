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
package de.interactive_instruments.etf.client.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.json.JSONObject;

import de.interactive_instruments.etf.client.TestCaseResult;
import de.interactive_instruments.etf.client.TestResult;
import de.interactive_instruments.etf.client.TestStepResult;

/**
 * @author Jon Herrmann ( herrmann aT interactive-instruments doT de )
 */
class TestCaseResultImpl extends AbstractResult implements TestCaseResult {

    private final Collection<TestStepResult> testStepResults;
    private final static String TEST_STEP_RESULT = "TestStepResult";
    private final static String TEST_CASE_RESULT = "TestCaseResult";
    private final static String TEST_STEP_RESULTS = "testStepResults";
    private final static String INVOKED_TESTS = "invokedTests";

    TestCaseResultImpl(final ResultCtx resultCtx) {
        super(resultCtx);

        testStepResults = (Collection<TestStepResult>) createTestStepResults(resultCtx.jsonObj);
    }

    private Collection<? extends TestResult> createTestStepResults(final JSONObject jsonObj) {
        if (jsonObj.isNull(TEST_STEP_RESULTS)) {
            return Collections.emptyList();
        }
        final Collection<JSONObject> childCollection = new JSONObjectOrArray(jsonObj.getJSONObject(TEST_STEP_RESULTS))
                .get(TEST_STEP_RESULT);
        final List<TestResult> testStepResults = new ArrayList<>();
        for (final JSONObject child : childCollection) {
            if (!child.isNull("resultedFrom") && !child.getJSONObject("resultedFrom").isNull("ref")) {
                testStepResults.add(doCreateChild(child));
                if (!child.isNull("invokedTests")) {
                    testStepResults.addAll(invokedTestStepResults(child));
                }
            }
        }
        return testStepResults;
    }

    private Collection<? extends TestResult> invokedTestStepResults(final JSONObject jsonObj) {
        final List<TestResult> invokedTestStepResults = new ArrayList<>();
        final JSONObjectOrArray invokedTests = new JSONObjectOrArray(jsonObj.getJSONObject(INVOKED_TESTS));
        final Collection<JSONObject> invokedTestStepResultCollection;
        if (invokedTests.has(TEST_STEP_RESULT)) {
            invokedTestStepResultCollection = invokedTests.get(TEST_STEP_RESULT);
        } else if (invokedTests.has(TEST_CASE_RESULT)) {
            final Collection<JSONObject> testCaseResults = invokedTests.get(TEST_CASE_RESULT);
            invokedTestStepResultCollection = new ArrayList<>();
            for (final JSONObject testCaseResult : testCaseResults) {
                if (testCaseResult.has(TEST_STEP_RESULTS)) {
                    final Collection<JSONObject> testStepSubCollection = new JSONObjectOrArray(
                            testCaseResult.getJSONObject(TEST_STEP_RESULTS)).get(TEST_STEP_RESULT);
                    invokedTestStepResultCollection.addAll(testStepSubCollection);
                }
            }
        } else {
            invokedTestStepResultCollection = Collections.EMPTY_LIST;
        }
        for (final JSONObject invokedTest : invokedTestStepResultCollection) {
            invokedTestStepResults.add(doCreateChild(invokedTest));
            if (!invokedTest.isNull("invokedTests")) {
                invokedTestStepResults.addAll(invokedTestStepResults(invokedTest));
            }
        }
        return invokedTestStepResults;
    }

    @Override
    TestResult doCreateChild(final JSONObject childJson) {
        return new TestStepResultImpl(resultCtx.newChild(childJson));
    }

    @Override
    public Collection<TestStepResult> testStepResults() {
        return this.testStepResults;
    }

    @Override
    public String type() {
        return TEST_CASE_RESULT;
    }

    @Override
    public Iterable<? extends TestResult> children() {
        return testStepResults;
    }

}
