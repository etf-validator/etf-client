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

import static de.interactive_instruments.etf.client.test.TestObjectTest.METADATA_TEST_URL;
import static org.junit.jupiter.api.Assertions.*;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;

import org.junit.jupiter.api.Test;

import de.interactive_instruments.etf.client.*;

/**
 * @author Jon Herrmann ( herrmann aT interactive-instruments doT de )
 */
public class ExecutableTestSuiteTest {

    public final static String ETS_ID = "EID59692c11-df86-49ad-be7f-94a1e1ddd8da";
    public final static String ETS_LABEL = "Common Requirements for ISO/TC 19139:2007 based INSPIRE metadata records.";

    public final static String ETS_ID_2 = "EIDe4a95862-9cc9-436b-9fdd-a0115d342350";
    public final static String ETS_LABEL_2 = "Conformance Class 1: INSPIRE data sets and data set series baseline metadata.";

    @Test
    void findEtsInCollection() throws RemoteInvocationException {
        final EtfEndpoint etfEndpoint = Constants.ETF_ENDPOINT;
        assertNotNull(etfEndpoint);

        boolean etsFound = false;
        for (final ExecutableTestSuite ets : etfEndpoint.executableTestSuites()) {
            assertNotNull(ets.eid());
            assertNotNull(ets.label());
            assertNotNull(ets.description());
            if (ets.label().equals(ETS_LABEL)) {
                etsFound = true;
                assertEquals(ETS_ID, ets.eid());
                assertEquals(ETS_LABEL, ets.label());
            }
        }
        assertTrue(etsFound);
    }

    @Test
    void findEtsById() throws RemoteInvocationException {
        final EtfEndpoint etfEndpoint = Constants.ETF_ENDPOINT;
        assertNotNull(etfEndpoint);

        final ExecutableTestSuite commonMetadataEts = etfEndpoint.executableTestSuites().itemById(ETS_ID).get();
        assertEquals(ETS_ID, commonMetadataEts.eid());
        assertEquals(ETS_LABEL, commonMetadataEts.label());
        assertNotNull(commonMetadataEts.description());

        boolean etsFound = false;
        for (final ExecutableTestSuite ets : etfEndpoint.executableTestSuites()) {
            assertNotNull(ets.eid());
            assertNotNull(ets.label());
            assertNotNull(ets.description());
            if (ets.label().equals(ETS_LABEL)) {
                etsFound = true;
                assertEquals(ETS_ID, ets.eid());
                assertEquals(ETS_LABEL, ets.label());
                assertEquals(commonMetadataEts, ets);
            }
        }
        assertTrue(etsFound);
    }

    @Test
    void findEtsByLabel() throws RemoteInvocationException {
        final EtfEndpoint etfEndpoint = Constants.ETF_ENDPOINT;
        assertNotNull(etfEndpoint);

        assertEquals(
                etfEndpoint.executableTestSuites().itemById(ETS_ID),
                etfEndpoint.executableTestSuites().itemByLabel(ETS_LABEL));

        assertFalse(etfEndpoint.executableTestSuites().itemByLabel("unknown").isPresent());
    }

    @Test
    void findMultipleEts() throws RemoteInvocationException {
        final EtfEndpoint etfEndpoint = Constants.ETF_ENDPOINT;
        assertNotNull(etfEndpoint);

        final EtsCollection metadataEtss = etfEndpoint.executableTestSuites().itemsById(ETS_ID, ETS_ID_2);
        assertEquals(2, metadataEtss.size());

        boolean etsFound1 = false;
        boolean etsFound2 = false;
        for (final ExecutableTestSuite ets : etfEndpoint.executableTestSuites()) {
            if (ets.label().equals(ETS_LABEL)) {
                etsFound1 = true;
                assertEquals(ETS_ID, ets.eid());
            }
            if (ets.label().equals(ETS_LABEL_2)) {
                etsFound2 = true;
                assertEquals(ETS_ID_2, ets.eid());
            }
        }
        assertTrue(etsFound1);
        assertTrue(etsFound2);
    }

    @Test
    void etsNotFoundWithItemsById() {
        final EtfEndpoint etfEndpoint = Constants.ETF_ENDPOINT;
        assertThrows(IllegalArgumentException.class, () -> {
            etfEndpoint.executableTestSuites().itemsById("unknown");
        });
    }

    @Test
    void etsNotFound() throws RemoteInvocationException {
        final EtfEndpoint etfEndpoint = Constants.ETF_ENDPOINT;
        assertTrue(etfEndpoint.executableTestSuites().itemById("").isEmpty());
    }

    @Test
    void findMultipleEtsByTag() throws RemoteInvocationException {
        final EtfEndpoint etfEndpoint = Constants.ETF_ENDPOINT;

        final Tag metadataTag = etfEndpoint.tags().itemById(TagTest.METADATA_TAG_ID).get();
        assertNotNull(metadataTag);

        final EtsCollection metadataEtss = etfEndpoint.executableTestSuites().itemsByTag(metadataTag);
        assertNotNull(metadataEtss);
        assertEquals(9, metadataEtss.size());

        boolean etsFound1 = false;
        boolean etsFound2 = false;
        for (final ExecutableTestSuite ets : metadataEtss) {
            if (ets.label().equals(ETS_LABEL)) {
                etsFound1 = true;
                assertEquals(ETS_ID, ets.eid());
            }
            if (ets.label().equals(ETS_LABEL_2)) {
                etsFound2 = true;
                assertEquals(ETS_ID_2, ets.eid());
            }
        }
        assertTrue(etsFound1);
        assertTrue(etsFound2);

        assertNotNull(metadataEtss.itemsById(ETS_ID));
        assertNotNull(metadataEtss.itemByLabel(ETS_LABEL));

        assertNotNull(metadataEtss.itemsById(ETS_ID_2));
        assertNotNull(metadataEtss.itemByLabel(ETS_LABEL_2));
    }

    @Test
    void startSingleEts() throws RemoteInvocationException, MalformedURLException, InterruptedException {
        final EtfEndpoint etfEndpoint = Constants.ETF_ENDPOINT;

        final ExecutableTestSuite metadataEts = etfEndpoint.executableTestSuites().itemById(ETS_ID).get();
        assertNotNull(metadataEts);

        final AdHocTestObject testObject = etfEndpoint.newAdHocTestObject().fromDataSet(new URL(METADATA_TEST_URL));
        assertNotNull(testObject);

        final TestRun testRun = metadataEts.execute(testObject);
        try {
            Thread.sleep(Duration.ofSeconds(5).toMillis());
            assertNotNull(testRun);
            final double progress = testRun.progress();
            assertTrue(progress <= 1.0);
            assertTrue(progress >= 0.0);
        } finally {
            testRun.cancel();
        }
    }

    @Test
    void startAndCancel() throws RemoteInvocationException, MalformedURLException, InterruptedException {
        final EtfEndpoint etfEndpoint = Constants.ETF_ENDPOINT;

        final ExecutableTestSuite metadataEts = etfEndpoint.executableTestSuites().itemById(ETS_ID).get();
        assertNotNull(metadataEts);

        final AdHocTestObject testObject = etfEndpoint.newAdHocTestObject().fromDataSet(new URL(METADATA_TEST_URL));
        assertNotNull(testObject);

        final TestRun testRun = metadataEts.execute(testObject);
        assertNotNull(testRun);
        Thread.sleep(2000);
        testRun.cancel();
    }

    @Test
    void startMultiple() throws RemoteInvocationException, MalformedURLException {
        final EtfEndpoint etfEndpoint = Constants.ETF_ENDPOINT;

        final Tag metadataTag = etfEndpoint.tags().itemById(TagTest.METADATA_TAG_ID).get();
        assertNotNull(metadataTag);

        final EtsCollection metadataEtss = etfEndpoint.executableTestSuites().itemsByTag(metadataTag);
        assertNotNull(metadataEtss);

        final AdHocTestObject testObject = etfEndpoint.newAdHocTestObject().fromDataSet(new URL(METADATA_TEST_URL));
        assertNotNull(testObject);

        final TestRun testRun = metadataEtss.execute(testObject);
        assertNotNull(testRun);
        final double progress = testRun.progress();
        assertTrue(progress <= 1.0);
        assertTrue(progress >= 0.0);
    }
}
