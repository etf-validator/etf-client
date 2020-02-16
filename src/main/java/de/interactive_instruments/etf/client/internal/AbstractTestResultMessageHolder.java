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

import org.json.JSONArray;
import org.json.JSONObject;

import de.interactive_instruments.etf.client.TestResultMessageHolder;

/**
 * @author Jon Herrmann ( herrmann aT interactive-instruments doT de )
 */
abstract class AbstractTestResultMessageHolder extends AbstractResult implements TestResultMessageHolder {

    private final Collection<String> messages;

    protected AbstractTestResultMessageHolder(final ResultCtx resultCtx) {
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
    public Collection<String> messages() {
        return this.messages;
    }
}
