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
package de.interactive_instruments.etf.client;

/**
 * Interface for accessing the service status of the connected instance.
 *
 * The remote ETF instance updates the status for the external WEB interface at certain intervals. The information can
 * therefore be between 20 seconds and 2 minutes old.
 *
 * @author Jon Herrmann ( herrmann aT interactive-instruments doT de )
 */
public interface EtfStatus {

    /**
     * The name of the used instance
     *
     * @return instance name as string
     */
    String name();

    /**
     * Status of the instance.
     *
     * Values including GOOD, MINOR, MAJOR
     *
     * @return status as string
     */
    String status();

    /**
     * Version of the instance
     *
     * @return version as string
     */
    String version();

    /**
     * Uptime of the instance in seconds
     *
     * @return uptime in seconds as long value
     */
    long uptime();

    /**
     * Get memory usage as a double in the [0.0,1.0] interval.
     *
     * @return the recent memory usage for the remote validator; a negative value if not available.
     */
    double memoryUsage();

    /**
     * Get disk usage as a double in the [0.0,1.0] interval.
     *
     * @return the recent disk usage for the remote validator; a negative value if not available.
     */
    double diskUsage();

    /**
     * Get cpu load as a double in the [0.0,1.0] interval.
     *
     * @return the recent CPU usage for the remote validator; a negative value if not available.
     */
    double cpuLoad();
}
