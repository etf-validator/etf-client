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

import java.util.*;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONObject;

import de.interactive_instruments.etf.client.ReferenceError;
import de.interactive_instruments.etf.client.RunParameters;
import de.interactive_instruments.etf.client.TestRunParameterException;

/**
 *
 *
 * @author Jon Herrmann ( herrmann aT interactive-instruments doT de )
 */
public class RunParametersImpl implements RunParameters {

    final Map<String, String> parameters;
    // in ETF a parameter can be required and/or static.
    // In this simplified model static and required parameters are not required
    final Set<String> required;
    final Set<String> statics;
    final String labelSuffix;

    private RunParametersImpl(final Map<String, String> map, final Set<String> required, final Set<String> statics) {
        this.parameters = new HashMap<>(Objects.requireNonNullElse(map, Collections.EMPTY_MAP));
        this.parameters.putIfAbsent("AssertionsToExecute", ".*");
        final Set<String> cleanRequired = new HashSet<>(required);
        cleanRequired.removeAll(statics);
        this.required = cleanRequired;
        this.statics = statics;
        this.labelSuffix = null;
    }

    private RunParametersImpl(final RunParametersImpl current, final Map<String, String> parameters) {
        this(merge(current, parameters), current.required, current.statics);
    }

    // Copy CTOR
    private RunParametersImpl(final String labelSuffix, final Map<String, String> parameters, final Set<String> required,
            final Set<String> statics) {
        this.labelSuffix = labelSuffix;
        this.parameters = parameters;
        this.required = required;
        this.statics = statics;
    }

    static Map<String, String> merge(final RunParametersImpl current, final Map<String, String> parameters) {
        final Map<String, String> mergedMap = new HashMap<>(current.map());
        final Map<String, String> currentParams = current.map();
        for (final Map.Entry<String, String> kvP : parameters.entrySet()) {
            if (current.statics().contains(kvP.getKey())) {
                throw new ReferenceError("You can not overwrite the static parameter '" + kvP.getKey() + "'");
            }
            if (!currentParams.containsKey(kvP.getKey())) {
                throw new ReferenceError("The referenced Parameter '" + kvP.getKey() + "' is unknown");
            }
            if (!current.statics.contains(kvP.getKey())) {
                mergedMap.put(kvP.getKey(), kvP.getValue());
            }
        }
        return mergedMap;
    }

    static RunParameters merge(final Iterable<RunParameters> parameters) {
        final Map<String, String> allParams = new HashMap<>();
        final Set<String> allRequired = new HashSet<>();
        final Set<String> allStatics = new HashSet<>();
        for (final RunParameters parameter : parameters) {
            allParams.putAll(parameter.map());
            allRequired.addAll(parameter.required());
            allStatics.addAll(((RunParametersImpl) parameter).statics());
        }
        allStatics.forEach(allParams::remove);
        return new RunParametersImpl(allParams, allRequired, allStatics);
    }

    static RunParameters init(final JSONObject jsonObject) {
        if (jsonObject.has("ParameterList") && !jsonObject.isNull("ParameterList")) {
            final Set<String> allRequired = new HashSet<>();
            final Set<String> allStatics = new HashSet<>();
            final Map<String, String> parameterKvp;
            final Object parameters = jsonObject.getJSONObject("ParameterList").get("parameter");
            if (parameters instanceof JSONObject) {
                final JSONObject parameter = (JSONObject) parameters;
                if (!parameter.has("static") || !parameter.getBoolean("static")) {
                    final Object def = parameter.opt("defaultValue");
                    parameterKvp = Collections.singletonMap(
                            parameter.getString("name"),
                            def != null ? def.toString() : null);
                    if (def == null && parameter.optBoolean("required")) {
                        allRequired.add(parameter.getString("name"));
                    }
                } else {
                    allStatics.add(parameter.getString("name"));
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
                        if (def == null && parameter.optBoolean("required")) {
                            allRequired.add(parameter.getString("name"));
                        }
                    } else {
                        allStatics.add(parameter.getString("name"));
                    }
                }
            } else {
                parameterKvp = Collections.emptyMap();
            }
            return new RunParametersImpl(parameterKvp, allRequired, allStatics);
        }
        return new RunParametersImpl(Collections.emptyMap(), Collections.emptySet(), Collections.emptySet());
    }

    static Object toJson(final RunParameters actualParameters, final RunParameters referenceParameters) {
        if (actualParameters == null) {
            if (referenceParameters.required().isEmpty()) {
                return new JSONObject();
            } else {
                throw new TestRunParameterException("The required Test Run Parameters are not set: " +
                        String.join(", ", referenceParameters.required()));
            }
        } else {
            final Set<String> missing = new HashSet<>();
            for (final String required : referenceParameters.required()) {
                if (actualParameters.map().get(required) == null) {
                    missing.add(required);
                }
            }
            if (!missing.isEmpty()) {
                throw new TestRunParameterException("The required Test Run Parameters are not set: " +
                        String.join(", ", missing));
            }
            return actualParameters.map();
        }
    }

    @Override
    public RunParameters setFrom(final Map<String, String> map) {
        return new RunParametersImpl(this, map.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> b, HashMap::new)));
    }

    @Override
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

    @Override
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

    @Override
    public Map<String, String> map() {
        return Collections.unmodifiableMap(this.parameters);
    }

    @Override
    public Set<String> required() {
        return this.required;
    }

    @Override
    public RunParameters labelSuffix(final String testRunLabelSuffix) {
        if (testRunLabelSuffix == null || testRunLabelSuffix.trim().isEmpty()) {
            throw new IllegalArgumentException("The test run label suffix must not be null or empty");
        }
        final int max = 75;
        if (testRunLabelSuffix.length() > max) {
            throw new IllegalArgumentException("The test run label suffix must not be longer than " + max + " characters");
        }
        return new RunParametersImpl(testRunLabelSuffix, this.parameters, this.required, this.statics);
    }

    static String labelSuffix(RunParameters runParameters) {
        if (runParameters != null) {
            final RunParametersImpl p = (RunParametersImpl) runParameters;
            final String s = p.labelSuffix;
            if (s != null && !s.trim().isEmpty()) {
                return " - " + s;
            } else {
                return "";
            }
        }
        return "";
    }

    Set<String> statics() {
        return this.statics;
    }

}
