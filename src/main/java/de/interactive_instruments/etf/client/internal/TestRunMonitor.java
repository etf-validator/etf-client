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
package de.interactive_instruments.etf.client.internal;

import static de.interactive_instruments.etf.client.internal.TestRunCmd.PATH;

import java.net.URI;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

import org.json.JSONArray;
import org.json.JSONObject;

import de.interactive_instruments.etf.client.RemoteInvocationException;

/**
 * @author Jon Herrmann ( herrmann aT interactive-instruments doT de )
 */
class TestRunMonitor implements Runnable {

    private final static String suffix = "/progress";
    private final TestRunCmd callback;
    private final static int noOfFirstChecks = 8;
    private final static long firstChecksWaitTime = Duration.ofSeconds(9).toMillis();
    private final static long waitTime = Duration.ofSeconds(13).toMillis();
    private final static long maxIterations = Duration.ofHours(24).toMillis() / waitTime;
    private final JsonGetRequest jsonGetRequest;
    private final CreateResultCmd createResultCmd;
    private final String id;

    TestRunMonitor(final InstanceCtx ctx, final TestRunCmd callback,
            final String testRunEid, final CreateResultCmd createResultCmd) {
        this.callback = callback;
        this.createResultCmd = createResultCmd;
        this.id = ctx.sessionId + "-" + testRunEid;
        jsonGetRequest = new JsonGetRequest(URI.create(
                ctx.baseUrl.toString() + "/" + PATH + testRunEid + suffix), ctx);
    }

    private boolean queryProgress() throws RemoteInvocationException {
        final JSONObject response = jsonGetRequest.query();
        final int val = response.getInt("val");
        final int max = response.getInt("max");
        final double progress = ((double) val) / max;
        final JSONArray logEntriesJson = response.getJSONArray("log");
        final List<String> logEntries = new ArrayList<>();
        for (final Object logEntry : logEntriesJson) {
            logEntries.add((String) logEntry);
        }
        this.callback.updateProgress(progress, logEntries);
        return val == max;
    }

    private void queryUntilFinished() throws TimeoutException, RemoteInvocationException {
        for (int i = 0; i < noOfFirstChecks; i++) {
            try {
                Thread.sleep(firstChecksWaitTime);
            } catch (InterruptedException ign) {
                // ignore
            }
            if (queryProgress()) {
                return;
            }
        }
        for (int i = 0; i < maxIterations; i++) {
            try {
                Thread.sleep(waitTime);
            } catch (InterruptedException ign) {
                // ignore
            }
            if (queryProgress()) {
                return;
            }
        }
        throw new TimeoutException("Validation process is taking too long");
    }

    @Override
    public void run() {
        try {
            Thread.currentThread().setName("etf-client-" + this.id);
            queryUntilFinished();
            this.callback.finished(createResultCmd.create());
        } catch (final Exception e) {
            this.callback.exception(e);
        }
    }
}
