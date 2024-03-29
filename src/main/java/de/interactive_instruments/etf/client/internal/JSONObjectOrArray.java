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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * @author Jon Herrmann ( herrmann aT interactive-instruments doT de )
 */
class JSONObjectOrArray {

    private final JSONObject jsonObject;

    JSONObjectOrArray(final JSONObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    Collection<JSONObject> get(final String name) {
        final Object arrayOrObj = jsonObject.get(name);
        if (arrayOrObj instanceof JSONObject) {
            return Collections.singleton((JSONObject) arrayOrObj);
        } else if (arrayOrObj instanceof JSONArray) {
            final JSONArray arr = (JSONArray) arrayOrObj;
            final Collection<JSONObject> collection = new ArrayList<>(arr.length());
            for (final Object o : arr) {
                collection.add((JSONObject) o);
            }
            return collection;
        } else {
            return Collections.EMPTY_LIST;
        }
    }

    public boolean has(final String name) {
        return jsonObject.has(name);
    }
}
