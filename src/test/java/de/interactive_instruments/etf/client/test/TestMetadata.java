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
package de.interactive_instruments.etf.client.test;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

import org.junit.jupiter.api.Test;

import de.interactive_instruments.etf.client.*;

/**
 * @author Jon Herrmann ( herrmann aT interactive-instruments doT de )
 */
public class TestMetadata {

    @Test
    void testIt() throws RemoteInvocationException, IOException, ExecutionException {
        final EtfEndpoint etfEndpoint = EtfValidatorClient.create().url(
                new URL("http://etf-dev-snapshots.interactive-instruments.de:32226/etf-webapp")).init();

        final EtsCollection ets = etfEndpoint.executableTestSuites();

        final AdHocTestObject testObject = etfEndpoint.newAdHocTestObject().fromService(
                new URL("http://tb17.geolabs.fr:8086/ogc-api/"));
        final TestRun testRun = ets.execute(testObject);

        for (final TestResult testResult : testRun.result()) {
            if (testResult instanceof TestCaseResult) {
                System.out.println(" " + testResult.label() + " - " + testResult.resultStatus());
            }
            if (testResult instanceof TestStepResult) {
                System.out.println("    " + testResult.label() + " - " + testResult.resultStatus());
            }

            /*
             * if (testResult instanceof TestResultMessageHolder) { final Collection<String> messages =
             * ((TestResultMessageHolder) testResult).messages(); for (final String message : messages) {
             * System.out.println(" - " + message); } }
             */
        }
    }

}
