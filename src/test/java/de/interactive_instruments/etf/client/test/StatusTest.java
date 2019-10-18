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

import java.net.MalformedURLException;
import java.net.URL;

import de.interactive_instruments.etf.client.EtfEndpoint;
import de.interactive_instruments.etf.client.EtfValidatorClient;
import de.interactive_instruments.etf.client.RemoteInvocationException;
import org.junit.jupiter.api.Test;

/**
 * @author Jon Herrmann ( herrmann aT interactive-instruments doT de )
 */
public class StatusTest {

    @Test
    void checkAvailable() throws MalformedURLException {
        final EtfEndpoint etfEndpoint = Constants.ETF_ENDPOINT;
        assertTrue(etfEndpoint.available());

        final EtfEndpoint endpointNotAvailable = EtfValidatorClient.create().url(new URL("http://example.org")).init();
        assertFalse(endpointNotAvailable.available());
    }

    @Test
    void checkStatus() throws RemoteInvocationException {
        final EtfEndpoint etfEndpoint = Constants.ETF_ENDPOINT;
        assertNotNull(etfEndpoint);
        assertNotNull(etfEndpoint.status());
        assertEquals("ETF", etfEndpoint.status().name());
    }

    @Test
    void checkStatusInformation() throws RemoteInvocationException {
        final EtfEndpoint etfEndpoint = Constants.ETF_ENDPOINT;
        assertNotNull(etfEndpoint);
        assertNotNull(etfEndpoint.status());
        assertTrue(etfEndpoint.status().cpuLoad() <= 1.0);
        assertTrue(etfEndpoint.status().diskUsage() <= 1.0);
        assertTrue(etfEndpoint.status().memoryUsage() <= 1.0);
        assertTrue(etfEndpoint.status().uptime() > 0);
        assertNotNull(etfEndpoint.status().version());
        assertTrue(etfEndpoint.status().version().startsWith("2."));
        assertNotNull(etfEndpoint.status().status());
    }

}
