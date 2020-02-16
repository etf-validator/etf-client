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

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Jon Herrmann ( herrmann aT interactive-instruments doT de )
 */
class JSONObjectWithOrderedAttributes extends JSONObject {

    private final Map<String, Object> map = new LinkedHashMap<>();

    @Override
    public Set<Map.Entry<String, Object>> entrySet() {
        return this.map.entrySet();
    }

    @Override
    public Object opt(String key) {
        return key == null ? null : this.map.get(key);
    }

    @Override
    public JSONObject put(final String key, final Object value) throws JSONException {
        if (key == null) {
            throw new NullPointerException("Null key.");
        }
        if (value != null) {
            testValidity(value);
            this.map.put(key, value);
        } else {
            this.remove(key);
        }
        return this;
    }

    @Override
    public boolean has(final String key) {
        return this.map.containsKey(key);
    }

    @Override
    public int length() {
        return this.map.size();
    }

    @Override
    public boolean isEmpty() {
        return this.map.isEmpty();
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
