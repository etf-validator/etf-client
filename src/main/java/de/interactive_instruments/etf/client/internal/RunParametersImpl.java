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
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONObject;

import de.interactive_instruments.etf.client.ReferenceError;
import de.interactive_instruments.etf.client.RunParameters;

/**
 *
 *
 * @author Jon Herrmann ( herrmann aT interactive-instruments doT de )
 */
public class RunParametersImpl implements RunParameters {

    final Map<String, String> parameters;

    private RunParametersImpl(final Map<String, String> parameters) {
        this.parameters = new HashMap<>(parameters);
    }

    static RunParametersImpl init(final Map<String, String> map) {
        return new RunParametersImpl(Objects.requireNonNullElse(map, Collections.EMPTY_MAP));
    }

    static RunParameters init(final JSONObject jsonObject) {
        if (jsonObject.has("ParameterList") && !jsonObject.isNull("ParameterList")) {
            final Map<String, String> parameterKvp;
            final Object parameters = jsonObject.getJSONObject("ParameterList").get("parameter");
            if (parameters instanceof JSONObject) {
                final JSONObject parameter = (JSONObject) parameters;
                if (!parameter.has("static") || !parameter.getBoolean("static")) {
                    final Object def = parameter.opt("defaultValue");
                    parameterKvp = Collections.singletonMap(
                            parameter.getString("name"),
                            def != null ? def.toString() : null);
                } else {
                    parameterKvp = Collections.emptyMap();
                }
            } else if (parameters instanceof JSONArray) {
                parameterKvp = new HashMap<>();
                for (final Object p : ((JSONArray) parameters)) {
                    final JSONObject parameter = (JSONObject) p;
                    if (!parameter.has("static") || !parameter.getBoolean("static")) {
                        final Object def = parameter.opt("defaultValue");
                        parameterKvp.put(
                                parameter.getString("name"),
                                def != null ? def.toString() : null);
                    }
                }
            } else {
                parameterKvp = Collections.emptyMap();
            }
            return RunParametersImpl.init(parameterKvp);
        }
        return RunParametersImpl.init(Collections.emptyMap());
    }

    private RunParametersImpl(final RunParametersImpl current, final Map<String, String> parameters) {
        final Map<String, String> currentParams = current.map();
        final Map<String, String> mergedMap = new HashMap<>(current.map());
        for (final Map.Entry<String, String> kvP : parameters.entrySet()) {
            if (!currentParams.containsKey(kvP.getKey())) {
                throw new ReferenceError("The referenced Parameter '" + kvP.getKey() + "' is unknown");
            }
            mergedMap.put(kvP.getKey(), kvP.getValue());
        }
        this.parameters = mergedMap;
    }

    public RunParameters setFrom(final Map<String, String> map) {
        return new RunParametersImpl(this, map.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> b, HashMap::new)));
    }

    public RunParameters setFrom(final String... strings) {
        if (strings != null) {
            if (strings.length % 2 != 0) {
                throw new IllegalArgumentException("Number of arguments is odd!");
            }
            final Map<String, String> map = new LinkedHashMap<>();
            for (int i = 0; i < strings.length; i += 2) {
                map.put(strings[i], strings[i + 1]);
            }
            return new RunParametersImpl(this, map);
        }
        return this;
    }

    public RunParameters setFrom(final Collection<String> strings) {
        if (strings != null) {
            if (strings.size() % 2 != 0) {
                throw new IllegalArgumentException("Number of arguments is odd!");
            }
            final Map<String, String> map = new LinkedHashMap<>();
            for (Iterator<String> it = strings.iterator(); it.hasNext();) {
                map.put(it.next(), it.next());
            }
            return new RunParametersImpl(this, map);
        }
        return this;
    }

    public Map<String, String> map() {
        return Collections.unmodifiableMap(this.parameters);
    }
}
