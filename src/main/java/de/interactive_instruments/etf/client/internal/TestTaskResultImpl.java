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

import java.util.*;

import org.json.JSONObject;

import de.interactive_instruments.etf.client.*;

/**
 * @author Jon Herrmann ( herrmann aT interactive-instruments doT de )
 */
class TestTaskResultImpl extends AbstractResult implements TestTaskResult {

    private final Collection<TestModuleResult> testModuleResults;

    TestTaskResultImpl(final ResultCtx resultCtx) {
        super(resultCtx.withAttachments());
        testModuleResults = (Collection<TestModuleResult>) createChildren(
                resultCtx.jsonObj, "testModuleResults", "TestModuleResult");
    }

    @Override
    TestResult doCreateChild(final JSONObject childJson) {
        return new TestModuleResultImpl(resultCtx.newChild(childJson));
    }

    @Override
    public Collection<TestModuleResult> testModuleResults() {
        return this.testModuleResults;
    }

    @Override
    public Optional<Collection<String>> internalErrors() {
        if (resultCtx.jsonObj.has("errorMessage")) {
            return Optional.of(Collections.singletonList(resultCtx.jsonObj.getString("errorMessage")));
        }
        if (resultStatus().equals(ResultStatus.INTERNAL_ERROR)) {
            final Set<String> errors = new LinkedHashSet<>();
            int c = 0;
            for (TestResult subResult : this) {
                if (subResult.resultStatus().equals(ResultStatus.INTERNAL_ERROR)
                        && subResult instanceof TestResultMessageHolder) {
                    errors.add(++c + " - Internal error in " + subResult.label());
                    errors.addAll(((TestResultMessageHolder) subResult).messages());
                }
            }
            return Optional.of(Collections.unmodifiableCollection(errors));
        }
        return Optional.empty();
    }

    @Override
    public String type() {
        return "TestTaskResult";
    }

    @Override
    public Iterable<? extends TestResult> children() {
        return testModuleResults;
    }
}
