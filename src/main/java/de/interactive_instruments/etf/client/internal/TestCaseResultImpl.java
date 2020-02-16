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

import java.util.Collection;

import org.json.JSONObject;

import de.interactive_instruments.etf.client.TestCaseResult;
import de.interactive_instruments.etf.client.TestResult;
import de.interactive_instruments.etf.client.TestStepResult;

/**
 * @author Jon Herrmann ( herrmann aT interactive-instruments doT de )
 */
class TestCaseResultImpl extends AbstractResult implements TestCaseResult {

    private final Collection<TestStepResult> testStepResults;

    TestCaseResultImpl(final ResultCtx resultCtx) {
        super(resultCtx);

        testStepResults = (Collection<TestStepResult>) createChildren(
                resultCtx.jsonObj, "testStepResults", "TestStepResult");
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
        return "TestCaseResult";
    }

    @Override
    public Iterable<? extends TestResult> children() {
        return testStepResults;
    }

}
