/**
 * Copyright 2017-2019 European Union, interactive instruments GmbH
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
 *
 * This work was supported by the EU Interoperability Solutions for
 * European Public Administrations Programme (http://ec.europa.eu/isa)
 * through Action 1.17: A Reusable INSPIRE Reference Platform (ARE3NA).
 */
package de.interactive_instruments.etf.client.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.json.JSONArray;
import org.json.JSONObject;

import de.interactive_instruments.etf.client.TestAssertionResult;
import de.interactive_instruments.etf.client.TestResult;

/**
 * @author Jon Herrmann ( herrmann aT interactive-instruments doT de )
 */
final class AssertionResultImpl extends AbstractResult implements TestAssertionResult {

    private final Collection<String> messages;

    AssertionResultImpl(final ResultCtx resultCtx) {
        super(resultCtx);
        if (resultCtx.jsonObj.has("messages")) {
            final Object childrenJson = resultCtx.jsonObj.getJSONObject("messages").get("message");
            if (childrenJson instanceof JSONArray) {
                final JSONArray childrenArray = (JSONArray) childrenJson;
                final Collection<String> messages = new ArrayList<>(((JSONArray) childrenJson).length());
                for (final Object o : childrenArray) {
                    messages.add(resultCtx.translate((JSONObject) o));
                }
                this.messages = Collections.unmodifiableCollection(messages);
            } else if (childrenJson instanceof JSONObject) {
                this.messages = Collections.singleton(resultCtx.translate((JSONObject) childrenJson));
            } else {
                this.messages = Collections.emptyList();
            }
        } else {
            this.messages = Collections.emptyList();
        }
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

    @Override
    public Collection<String> messages() {
        return this.messages;
    }
}
