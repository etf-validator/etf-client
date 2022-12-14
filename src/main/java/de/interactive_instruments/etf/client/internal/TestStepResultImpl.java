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
package de.interactive_instruments.etf.client.internal;

import java.util.Collection;
import java.util.Map;

import org.json.JSONObject;

import de.interactive_instruments.etf.client.TestAssertionResult;
import de.interactive_instruments.etf.client.TestResult;
import de.interactive_instruments.etf.client.TestStepResult;

/**
 * @author Jon Herrmann ( herrmann aT interactive-instruments doT de )
 */
class TestStepResultImpl extends AbstractTestResultMessageHolder implements TestStepResult {

    private final Collection<TestAssertionResult> testAssertionResult;
    private final Map<String, String> attachments;

    TestStepResultImpl(final ResultCtx resultCtx) {
        super(resultCtx);
        testAssertionResult = (Collection<TestAssertionResult>) createChildren(
                resultCtx.jsonObj, "testAssertionResults", "TestAssertionResult");
        attachments = createAttachments(resultCtx.jsonObj);
    }

    @Override
    TestResult doCreateChild(final JSONObject childJson) {
        return new TestAssertionResultImpl(resultCtx.newChild(childJson));
    }

    @Override
    public Map<String, String> getAttachment() {
        return this.attachments;
    }

    @Override
    public Collection<TestAssertionResult> testAssertionResults() {
        return this.testAssertionResult;
    }

    @Override
    public String type() {
        return "TestStepResult";
    }

    @Override
    public Iterable<? extends TestResult> children() {
        return testAssertionResult;
    }

    @Override
    public String label() {
        final String relabel = attachments.get("relabel");
        if (relabel != null) {
            return relabel;
        } else {
            return super.label();
        }
    }
}
