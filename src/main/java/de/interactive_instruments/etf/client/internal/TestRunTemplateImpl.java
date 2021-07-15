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

import org.json.JSONArray;
import org.json.JSONObject;

import de.interactive_instruments.etf.client.*;

/**
 * @author Jon Herrmann ( herrmann aT interactive-instruments doT de )
 */
public class TestRunTemplateImpl extends AbstractMetadata implements TestRunTemplate {

    private final TrtExecutionContext trtExecutionContext;
    private final TestObjectBaseType baseType;
    private final Collection<String> executableTestSuiteEids;
    private final RunParameters runParameters;

    public TestRunTemplateImpl(final TrtExecutionContext trtExecutionContext, final JSONObject jsonObject,
            final EtsCollection allExecutableTestSuites,
            final EtfCollection<TranslationTemplateBundle> translationTemplateBundleCollection) {
        super(jsonObject);
        this.trtExecutionContext = trtExecutionContext;

        final Object etsRefs = jsonObject.getJSONObject("executableTestSuites").get("executableTestSuite");
        if (etsRefs instanceof JSONObject) {
            executableTestSuiteEids = Collections.singleton(
                    eidFromUrl(((JSONObject) etsRefs).getString("href")));
        } else if (etsRefs instanceof JSONArray) {
            executableTestSuiteEids = new ArrayList<>();
            for (final Object tag : ((JSONArray) etsRefs)) {
                executableTestSuiteEids.add(
                        eidFromUrl(((JSONObject) tag).getString("href")));
            }
        } else {
            throw new ReferenceError(
                    "No Executable Test Suite found for Test Run Template " + eid());
        }
        final EtsCollection etsCollection = allExecutableTestSuites.itemsById(executableTestSuiteEids);
        this.baseType = etsCollection.iterator().next().supportedBaseType();

        final RunParameters testRunTemplateParameter = RunParametersImpl.init(jsonObject);
        final List<RunParameters> allParameters = new ArrayList<>();
        allParameters.add(testRunTemplateParameter);
        allParameters.add(etsCollection.parameters());
        this.runParameters = RunParametersImpl.merge(allParameters);
    }

    @Override
    public TestObjectBaseType supportedBaseType() {
        return this.baseType;
    }

    @Override
    public Collection<String> executableTestSuiteEids() {
        return Collections.unmodifiableCollection(executableTestSuiteEids);
    }

    @Override
    public TestRunCloseable execute(final TestObject testObject, final RunParameters parameters)
            throws RemoteInvocationException, IncompatibleTestObjectTypesException, EtfIllegalStateException {
        return (TestRunCloseable) execute(testObject, null, parameters);
    }

    @Override
    public TestRun execute(final TestObject testObject, final TestRunObserver testRunObserver, final RunParameters parameters)
            throws RemoteInvocationException, IncompatibleTestObjectTypesException, EtfIllegalStateException {
        if (!testObject.baseType().equals(this.baseType)) {
            throw new IncompatibleTestObjectTypesException();
        }
        return this.trtExecutionContext.start(this, testObject, testRunObserver, parameters);
    }

    @Override
    public RunParameters parameters() {
        return runParameters;
    }
}
