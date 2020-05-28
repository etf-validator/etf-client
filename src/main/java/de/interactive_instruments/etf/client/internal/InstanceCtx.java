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

import java.net.Authenticator;
import java.net.URI;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.Duration;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Jon Herrmann ( herrmann aT interactive-instruments doT de )
 */
final class InstanceCtx {

    final URI baseUrl;
    final Authenticator auth;
    final Locale locale;
    final Duration timeout;
    final String sessionId;
    final AtomicInteger requestNo = new AtomicInteger(1);
    final DecimalFormat floatFormat = new DecimalFormat("0.000", new DecimalFormatSymbols(Locale.ENGLISH));

    InstanceCtx(final URI baseUrl, final Authenticator auth, final Locale locale, final Duration timeout) {
        this.baseUrl = baseUrl;
        this.auth = auth;
        this.locale = locale;
        this.sessionId = UUID.randomUUID().toString();
        this.timeout = timeout;
    }

    int requestNo() {
        return requestNo.getAndIncrement();
    }
}
