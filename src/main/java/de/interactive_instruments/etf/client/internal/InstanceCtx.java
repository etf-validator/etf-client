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

import de.interactive_instruments.etf.client.EtfIllegalStateException;

import java.net.Authenticator;
import java.net.URI;
import java.text.DecimalFormat;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.*;
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
    private final DecimalFormat floatFormat;
    private final ExecutorService executor;
    private boolean shutdown = false;
    private final Set<TestRunCmd> testRuns = new ConcurrentSkipListSet<>();

    InstanceCtx(final ExecutorService executorService, final URI baseUrl, final Authenticator auth, final Locale locale,
            final Duration timeout, final DecimalFormat floatFormat) {
        if (executorService == null) {
            this.executor = new ThreadPoolExecutor(0, 256, 5,
                    TimeUnit.SECONDS, new SynchronousQueue<>());
        } else {
            this.executor = executorService;
        }
        this.baseUrl = baseUrl;
        this.auth = auth;
        this.locale = locale;
        this.sessionId = UUID.randomUUID().toString();
        this.timeout = timeout;
        this.floatFormat = floatFormat;
    }

    String format(final Object obj) {
        if (formatFloats()) {
            return floatFormat.format(obj);
        } else {
            return obj.toString();
        }
    }

    ExecutorService executor() {
        if(shutdown) {
            throw new EtfIllegalStateException("The connection to the endpoint with session ID '"
                    + this.sessionId + "' is closed");
        }
        return executor;
    }

    void registerRun(final TestRunCmd testRun) {
        this.testRuns.add(testRun);
    }

    void deregisterRun(final TestRunCmd testRun) {
        this.testRuns.remove(testRun);
    }

    synchronized void close() {
        shutdown = true;
        final Collection<TestRunCmd> testRunsCopy = new ArrayList<>(this.testRuns);
        for (final TestRunCmd run : testRunsCopy) {
            try {
                if (!run.finished()) {
                    run.cancel();
                }
            } catch (final Exception ignore) {}
        }
        this.testRuns.clear();
        executor.shutdown();
        try {
            if (!executor.awaitTermination(2, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException ignore) {
            executor.shutdownNow();
        }
    }

    boolean formatFloats() {
        return floatFormat != null;
    }

    int requestNo() {
        return requestNo.getAndIncrement();
    }
}
