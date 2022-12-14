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
import java.util.Optional;

/**
 * Represents an ETF response with a collection of items.
 *
 * @author Jon Herrmann ( herrmann aT interactive-instruments doT de )
 */
public interface EtfCollection<V> extends Iterable<V> {

    /**
     * Returns the metadata of the items in the collection
     *
     * @return collection of metadata
     */
    Collection<? extends ItemMetadata> metadata();

    /**
     * Get an item by its eid
     *
     * @param eid
     *            EID of the item
     * @return the item
     */
    Optional<V> itemById(final String eid);

    /**
     * Get an item by its label
     *
     * @param label
     *            label of the item
     * @return the item
     */
    Optional<V> itemByLabel(final String label);

    /**
     * Returns the size of this collection
     *
     * @return size of the collection
     */
    default int size() {
        return metadata().size();
    }
}
