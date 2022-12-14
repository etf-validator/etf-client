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

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;

import de.interactive_instruments.etf.client.EtfEndpoint;
import de.interactive_instruments.etf.client.EtfValidatorClient;

/**
 * @author Jon Herrmann ( herrmann aT interactive-instruments doT de )
 */
public class Constants {

    final static URL ETF_TEST_SERVICE;
    static {
        try {
            ETF_TEST_SERVICE = new URL("http://staging-inspire-validator.eu-west-1.elasticbeanstalk.com/validator/");
        } catch (MalformedURLException e) {
            throw new RuntimeException();
        }
    }

    public final static EtfEndpoint ETF_ENDPOINT = EtfValidatorClient.create().url(ETF_TEST_SERVICE)
            .retryOnConnectionReset(Duration.ofSeconds(1), 4).init();

    public final static EtfEndpoint create() {
        return EtfValidatorClient.create().url(ETF_TEST_SERVICE)
                .retryOnConnectionReset(Duration.ofSeconds(20), 4).init();
    }
}
