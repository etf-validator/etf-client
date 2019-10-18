/*
 * Copyright 2010-2019 interactive instruments GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.interactive_instruments.etf.client.test;

import static de.interactive_instruments.etf.client.test.TestObjectTest.METADATA_TEST_URL;
import static org.junit.jupiter.api.Assertions.*;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.concurrent.ExecutionException;

import de.interactive_instruments.etf.client.*;
import org.junit.jupiter.api.Test;

/**
 * @author Jon Herrmann ( herrmann aT interactive-instruments doT de )
 */
public class TestResultTest {

    @Test
    void startMultipleTests() throws RemoteInvocationException, MalformedURLException, ExecutionException {
        final EtfEndpoint etfEndpoint = Constants.ETF_ENDPOINT;

        final Tag metadataTag = etfEndpoint.tags().itemById(TagTest.METADATA_TAG_ID).get();
        assertNotNull(metadataTag);

        final EtsCollection metadataTestSuites = etfEndpoint.executableTestSuites().itemsByTag(metadataTag);
        assertNotNull(metadataTestSuites);

        final AdHocTestObject testObject = etfEndpoint.newAdHocTestObject().fromDataSet(new URL(METADATA_TEST_URL));
        assertNotNull(testObject);

        final TestRun testRun = metadataTestSuites.execute(testObject);
        assertNotNull(testRun);
        try {
            final double progress = testRun.progress();
            assertTrue(progress <= 1.0);
            assertTrue(progress >= 0.0);

            boolean assertionFound = false;
            final TestRunResult result = testRun.result();
            assertEquals(testRun.progress(), 1.0);
            for (final TestResult testResult : result) {
                assertNotNull(testResult.label());
                assertNotNull(testResult.description());
                assertTrue(testResult.duration() >= 0);
                assertNotNull(testResult.resultStatus());
                assertNotSame(testResult.resultStatus(), ResultStatus.OTHER);
                assertNotSame(testResult.resultStatus(), ResultStatus.UNDEFINED);
                if (testResult instanceof TestStepResult) {
                    for (final String value : ((TestStepResult) testResult).getAttachment().values()) {
                        System.out.println(" - " + value);
                    }
                }
                if (testResult instanceof TestAssertionResult) {
                    assertionFound = true;
                    final Collection<String> messages = ((TestAssertionResult) testResult).messages();
                    for (final String message : messages) {
                        System.out.println(" - " + message);
                    }
                }
                assertNotNull(testResult.startDate());
                assertNotNull(testResult.type());
                System.out.println(testResult.type() + " - " + testResult.label() + " - " + testResult.resultStatus());
            }
            assertTrue(assertionFound);
            assertNotNull(result.logEntries());
            assertFalse(result.logEntries().isEmpty());
        } finally {
            testRun.cancel();
        }
    }
}
