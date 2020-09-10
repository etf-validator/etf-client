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

import static java.util.regex.Pattern.CASE_INSENSITIVE;

import java.util.*;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;

import de.interactive_instruments.etf.client.*;

/**
 * @author Jon Herrmann ( herrmann aT interactive-instruments doT de )
 */
final class ExecutableTestSuiteImpl extends AbstractMetadata implements ExecutableTestSuite {

    private final EtsExecutionContext etsExecutionContext;
    private final TestObjectBaseType baseType;
    private final Collection<String> tagEids;
    private final TranslationTemplateBundle bundle;
    private final EidObjectMapping eidObjectMappings;
    private final RunParameters runParameters;
    private final static Pattern floatingTokensPattern = Pattern.compile(".*coord.*|.*distance.*|.*height.*|.*meter.*",
            CASE_INSENSITIVE);

    ExecutableTestSuiteImpl(final EtsExecutionContext etsExecutionContext, final JSONObject jsonObject,
            final EtfCollection<TranslationTemplateBundle> translationTemplateBundleCollection) {
        super(jsonObject);
        this.etsExecutionContext = etsExecutionContext;
        final String testDriverRef = jsonObject.getJSONObject("testDriver").getString("href");
        // fastest approach in the INSPIRE validator environment
        if (testDriverRef.contains("4dddc9e2-1b21-40b7-af70-6a2d156ad130")
                || testDriverRef.contains("c8f39ab3-b0e4-4e06-924a-f31cc99a4932")) {
            baseType = TestObjectBaseType.DATA_SET;
        } else {
            baseType = TestObjectBaseType.SERVICE;
        }
        if (jsonObject.has("tags")) {
            final Object tags = jsonObject.getJSONObject("tags").get("tag");
            if (tags instanceof JSONObject) {
                tagEids = Collections.singleton(
                        eidFromUrl(((JSONObject) tags).getString("href")));
            } else if (tags instanceof JSONArray) {
                tagEids = new ArrayList<>();
                for (final Object tag : ((JSONArray) tags)) {
                    tagEids.add(
                            eidFromUrl(((JSONObject) tag).getString("href")));
                }
            } else {
                tagEids = Collections.emptyList();
            }
        } else {
            tagEids = Collections.emptyList();
        }
        final String translationTemplateEid = eidFromUrl(jsonObject.getJSONObject(
                "translationTemplateBundle").getString("href"));
        final Optional<TranslationTemplateBundle> b = translationTemplateBundleCollection.itemById(translationTemplateEid);
        if (b.isEmpty()) {
            throw new ReferenceError("Translation Template Bundle '" + translationTemplateEid +
                    "' not found. Please contact the administrator of the service to ensure "
                    + "that the Translation Template Bundle is installed correctly. "
                    + "If the problem persists, the Executable Test Suite developer should be "
                    + "notified of the missing translation.");
        }
        bundle = b.get();

        final EidObjectMappingBuilder mappingBuilder = new EidObjectMappingBuilder();
        final String[] names = {
                "testModules", "TestModule",
                "testCases", "TestCase",
                "testSteps", "TestStep",
                "testAssertions", "TestAssertion"
        };
        mappingBuilder.add(jsonObject);
        createMappings(mappingBuilder, jsonObject, names, 0);
        eidObjectMappings = mappingBuilder.build();
        runParameters = RunParametersImpl.init(jsonObject);
    }

    private void createMappings(final EidObjectMappingBuilder mappingBuilder, final JSONObject jsonObj,
            final String[] names, final int pos) {
        if (pos < names.length && jsonObj.has(names[pos])) {
            final Collection<JSONObject> children = new JSONObjectOrArray(
                    jsonObj.getJSONObject(names[pos])).get(names[pos + 1]);
            for (final Object o : children) {
                mappingBuilder.add((JSONObject) o);
                createMappings(mappingBuilder, (JSONObject) o, names, pos + 2);
            }
        }
    }

    @Override
    public TestObjectBaseType supportedBaseType() {
        return this.baseType;
    }

    @Override
    public Collection<String> tagEids() {
        return tagEids;
    }

    String translate(final Locale locale, final JSONObject messageJson) {
        final String name = messageJson.getString("ref");
        final Map<String, String> parameterMap;
        if (messageJson.has("translationArguments")) {
            parameterMap = new HashMap<>();
            final JSONObject translationArguments = messageJson.getJSONObject("translationArguments");
            final Collection<JSONObject> arguments = new JSONObjectOrArray(translationArguments).get("argument");
            for (final JSONObject argument : arguments) {
                if (!argument.has("token")) {
                    throw new ReferenceError("No token provided in the Translation Argument. "
                            + "This is most likely a bug in the Executable Test Suite. Translation Argument: "
                            + argument.toString());
                }
                if (argument.has("$")) {
                    final Object vO = argument.get("$");
                    final String token = argument.getString("token");
                    final String value;
                    if (vO instanceof Number) {
                        if (vO instanceof Double || vO instanceof Float || floatingTokensPattern.matcher(token).matches()) {
                            value = etsExecutionContext.instanceCtx.floatFormat.format(vO);
                        } else {
                            value = vO.toString();
                        }
                    } else {
                        value = vO.toString();
                    }
                    parameterMap.put(token, value);
                } else {
                    // The necessary information is missing in the error message. Hopefully the user will understand the
                    // error...
                    parameterMap.put(argument.getString("token"), "");
                }
            }
        } else {
            parameterMap = null;
        }
        return bundle.translate(locale.getLanguage(), name, parameterMap);
    }

    @Override
    public TestRun execute(final TestObject testObject, RunParameters parameters)
            throws RemoteInvocationException, IncompatibleTestObjectTypesException {
        return execute(testObject, null, parameters);
    }

    @Override
    public TestRun execute(final TestObject testObject, final TestRunObserver testRunObserver, final RunParameters parameters)
            throws RemoteInvocationException, IncompatibleTestObjectTypesException {
        if (!testObject.baseType().equals(this.baseType)) {
            throw new IncompatibleTestObjectTypesException();
        }
        return this.etsExecutionContext.start(Collections.singleton(this), testObject, testRunObserver, parameters);
    }

    @Override
    public RunParameters parameters() {
        return this.runParameters;
    }

    EidObjectMapping objectMapping() {
        return eidObjectMappings;
    }
}
