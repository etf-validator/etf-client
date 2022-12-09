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
package de.interactive_instruments.etf.client.test;

import static org.junit.jupiter.api.Assertions.*;

import java.net.MalformedURLException;
import java.net.URL;

import org.junit.jupiter.api.Test;

import de.interactive_instruments.etf.client.EtfEndpoint;
import de.interactive_instruments.etf.client.EtfValidatorClient;
import de.interactive_instruments.etf.client.RemoteInvocationException;

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
        // assertEquals("ETF", etfEndpoint.status().name());
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
