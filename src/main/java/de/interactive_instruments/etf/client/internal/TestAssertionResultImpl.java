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

import java.util.Collections;

import org.json.JSONObject;

import de.interactive_instruments.etf.client.TestAssertionResult;
import de.interactive_instruments.etf.client.TestResult;

/**
 * @author Jon Herrmann ( herrmann aT interactive-instruments doT de )
 */
final class TestAssertionResultImpl extends AbstractTestResultMessageHolder implements TestAssertionResult {

    TestAssertionResultImpl(final ResultCtx resultCtx) {
        super(resultCtx);
    }

    @Override
    TestResult doCreateChild(final JSONObject childJson) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String type() {
        return "TestAssertionResult";
    }

    @Override
    public Iterable<? extends TestResult> children() {
        return Collections.emptyList();
    }
}
