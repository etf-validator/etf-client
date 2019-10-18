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

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;

import de.interactive_instruments.etf.client.AdHocTestObject;
import de.interactive_instruments.etf.client.EtfEndpoint;
import de.interactive_instruments.etf.client.RemoteInvocationException;
import de.interactive_instruments.etf.client.TestObjectBaseType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

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
