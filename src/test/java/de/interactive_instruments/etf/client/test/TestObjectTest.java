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
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import de.interactive_instruments.etf.client.AdHocTestObject;
import de.interactive_instruments.etf.client.EtfEndpoint;
import de.interactive_instruments.etf.client.RemoteInvocationException;
import de.interactive_instruments.etf.client.TestObjectBaseType;

/**
 * @author Jon Herrmann ( herrmann aT interactive-instruments doT de )
 */
public class TestObjectTest {

    public final static String METADATA_TEST_URL = "https://raw.githubusercontent.com/jonherrmann/community/master/examples/Dataset_metadata_2.0_example.xml";

    @Test
    void referenceDatasetTest() throws MalformedURLException, RemoteInvocationException {
        final EtfEndpoint etfEndpoint = Constants.ETF_ENDPOINT;
        assertNotNull(etfEndpoint);
        assertNotNull(etfEndpoint.newAdHocTestObject());
        final AdHocTestObject testObject = etfEndpoint.newAdHocTestObject().fromDataSet(new URL(METADATA_TEST_URL));
        assertNotNull(testObject);
        Assertions.assertEquals(testObject.baseType(), TestObjectBaseType.DATA_SET);
        // forwards the call to toJson()
        assertNotNull(testObject.toString());
    }

    @Test
    void uploadFileTest() throws IOException, RemoteInvocationException {
        final EtfEndpoint etfEndpoint = Constants.ETF_ENDPOINT;
        final Path file = Path.of("src/test/resources/Dataset_metadata_2.0_example.xml");
        assertTrue(file.toFile().exists());
        final AdHocTestObject testObject = etfEndpoint.newAdHocTestObject().fromDataSet(file);
        assertNotNull(testObject);
        assertNotNull(testObject.eid());
    }
}
