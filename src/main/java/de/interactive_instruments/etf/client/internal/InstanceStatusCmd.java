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

import java.net.URI;
import java.text.DecimalFormat;

import org.json.JSONObject;

import de.interactive_instruments.etf.client.EtfStatus;
import de.interactive_instruments.etf.client.RemoteInvocationException;

/**
 * @author Jon Herrmann ( herrmann aT interactive-instruments doT de )
 */
class InstanceStatusCmd {

    private final static String PATH = "/status";

    private static class CurrentEtfStatus implements EtfStatus {
        private final String name;
        private final String status;
        private final String version;
        private final long uptime;
        private final double memoryUsage;
        private final double diskUsage;
        private final double cpuLoad;

        private CurrentEtfStatus(final JSONObject jsonObject) {
            this.name = jsonObject.getString("name");
            this.status = jsonObject.getString("status");
            this.version = jsonObject.getString("version");
            this.uptime = jsonObject.getLong("uptime");
            final long presumableFreeMemory = jsonObject.getLong("presumableFreeMemory");
            final long allocatedMemory = jsonObject.getLong("allocatedMemory");
            final long maxMemory = presumableFreeMemory + allocatedMemory;
            this.memoryUsage = ((double) allocatedMemory) / maxMemory;
            final long totalSpace = jsonObject.getLong("totalSpace");
            final long freeSpace = jsonObject.getLong("freeSpace");
            final long usedSpace = totalSpace - freeSpace;
            this.diskUsage = ((double) usedSpace) / freeSpace;
            this.cpuLoad = jsonObject.getDouble("cpuLoad");
        }

        public String name() {
            return name;
        }

        public String status() {
            return status;
        }

        public String version() {
            return version;
        }

        public long uptime() {
            return uptime;
        }

        public double memoryUsage() {
            return memoryUsage;
        }

        public double diskUsage() {
            return diskUsage;
        }

        public double cpuLoad() {
            return cpuLoad;
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("{");
            sb.append("name='").append(name).append('\'');
            sb.append(", status='").append(status).append('\'');
            sb.append(", version='").append(version).append('\'');
            sb.append(", uptime=").append(uptime);
            final DecimalFormat df = new DecimalFormat("0.00");
            sb.append(", memoryUsage=").append(df.format(memoryUsage));
            sb.append(", diskUsage=").append(df.format(diskUsage));
            sb.append(", cpuLoad=").append(df.format(cpuLoad));
            sb.append('}');
            return sb.toString();
        }
    }

    private final JsonGetRequest apiCall;

    InstanceStatusCmd(final InstanceCtx ctx) {
        this.apiCall = new JsonGetRequest(URI.create(ctx.baseUrl.toString() + PATH), ctx);
    }

    EtfStatus query() throws RemoteInvocationException {
        return new CurrentEtfStatus(apiCall.query());
    }
}
