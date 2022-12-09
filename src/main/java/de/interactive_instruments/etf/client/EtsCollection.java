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

import java.util.Collection;

/**
 * Represents an ETF response with a collection of Executable Test Suites.
 *
 * @author Jon Herrmann ( herrmann aT interactive-instruments doT de )
 */
public interface EtsCollection extends EtfCollection<ExecutableTestSuite>, TestRunExecutable {

    /**
     * Filter by a specific Tag
     *
     * @param tag
     *            the Tag to filter the ETS
     *
     * @return a filtered EtsCollection
     */
    EtsCollection itemsByTag(final Tag tag);

    /**
     * Filter multiple Executable Test Suites by their ID
     *
     * @param eids
     *            array of EIDs
     * @return a filtered EtsCollection
     *
     * @throws IllegalArgumentException
     *             if an ETS with the EID could not be found
     */
    EtsCollection itemsById(final String... eids);

    /**
     * Filter multiple Executable Test Suites by their ID
     *
     * @param eids
     *            collection of EIDs
     * @return a filtered EtsCollection
     *
     * @throws IllegalArgumentException
     *             if an ETS with the EID could not be found
     */
    default EtsCollection itemsById(final Collection<String> eids) {
        return itemsById(eids.toArray(new String[0]));
    }
}
