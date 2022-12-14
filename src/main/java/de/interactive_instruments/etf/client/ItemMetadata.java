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

import java.util.Optional;

/**
 * Basic Metadata of an item
 *
 * @author Jon Herrmann ( herrmann aT interactive-instruments doT de )
 */
public interface ItemMetadata {

    /**
     * EID of an item
     *
     * @return as non-null string
     */
    String eid();

    /**
     * Label of an item
     *
     * @return as a non-null string
     */
    String label();

    /**
     * Description of an item
     *
     * @return as non-null string
     */
    String description();

    /**
     * Version of an item
     *
     * @return optional String
     */
    default Optional<String> version() {
        return Optional.empty();
    }
}
