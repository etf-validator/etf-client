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

import de.interactive_instruments.etf.client.EtfEndpoint;
import de.interactive_instruments.etf.client.EtfValidatorClient;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author Jon Herrmann ( herrmann aT interactive-instruments doT de )
 */
public class Constants {

    final static URL ETF_TEST_SERVICE;
    static {
        try {
            ETF_TEST_SERVICE = new URL("http://inspire.ec.europa.eu/validator/");
        } catch (MalformedURLException e) {
            throw new RuntimeException();
        }
    }

    public final static EtfEndpoint ETF_ENDPOINT = EtfValidatorClient.create().url(ETF_TEST_SERVICE).init();
}
