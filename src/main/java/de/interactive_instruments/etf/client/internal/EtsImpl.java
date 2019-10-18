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

import java.util.*;
import java.util.concurrent.ExecutorService;

import org.json.JSONArray;
import org.json.JSONObject;

import de.interactive_instruments.etf.client.*;

/**
 * @author Jon Herrmann ( herrmann aT interactive-instruments doT de )
 */
final class EtsImpl extends Metadata implements ExecutableTestSuite {

    private final ExecutorService executor;
    private final TestObjectBaseType baseType;
    private final InstanceCtx ctx;
    private final Collection<String> tagEids;
    private final TranslationTemplateBundle bundle;
    private final EidObjectMapping eidObjectMappings;

    EtsImpl(final InstanceCtx ctx, final JSONObject jsonObject, final ExecutorService executor,
            final EtfCollection<TranslationTemplateBundle> translationTemplateBundleCollection) {
        super(jsonObject);
        this.ctx = ctx;
        this.executor = executor;
        final String testDriverRef = jsonObject.getJSONObject("testDriver").getString("href");
        // fastest approach in the INSPIRE validator environment
        if (testDriverRef.contains("4dddc9e2-1b21-40b7-af70-6a2d156ad130")) {
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

    private static String eidFromUrl(final String url) {
        final int lastBackslash = url.lastIndexOf('/');
        final int p = url.lastIndexOf('.');
        final String eid;
        if (lastBackslash >= 0) {
            if (p > 0) {
                eid = url.substring(url.lastIndexOf('/') + 1, p);
            } else {
                eid = url.substring(url.lastIndexOf('/') + 1);
            }
        } else {
            eid = url;
        }
        if (eid.startsWith("EID")) {
            return eid;
        }
        return "EID" + eid;
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
                parameterMap.put(argument.getString("token"), argument.get("$").toString());
            }
        } else {
            parameterMap = null;
        }
        return bundle.translate(locale.getLanguage(), name, parameterMap);
    }

    @Override
    public TestRun execute(final TestObject testObject) throws RemoteInvocationException, IncompatibleTestObjectTypes {
        return execute(testObject, null);
    }

    @Override
    public TestRun execute(final TestObject testObject, final TestRunObserver testRunObserver)
            throws RemoteInvocationException, IncompatibleTestObjectTypes {
        if (!testObject.baseType().equals(this.baseType)) {
            throw new IncompatibleTestObjectTypes();
        }
        return TestRunCmd.prepare(this.ctx, executor, Collections.singleton(this), testObject, testRunObserver);
    }

    EidObjectMapping objectMapping() {
        return eidObjectMappings;
    }
}
