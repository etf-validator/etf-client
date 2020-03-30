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
package de.interactive_instruments.etf.client;

import java.util.*;

/**
 * Parameters for a Test Run
 *
 * @author Jon Herrmann ( herrmann aT interactive-instruments doT de )
 */
public interface RunParameters {

    /**
     * Set the actual Parameters from a String array.
     *
     * Values with an even index must reference the parameter name, the following odd index set the corresponding value.
     *
     * @param strings
     *            String array
     *
     * @throws IllegalArgumentException
     *             if the array has a odd number of values
     * @throws ReferenceError
     *             if the referenced Parameter name is unknown
     *
     * @return immutable RunParameters object
     */
    RunParameters setFrom(final String... strings);

    /**
     * Set the actual Parameters from a String collection.
     *
     * Values with an even index must reference the parameter name, the following odd index set the corresponding value.
     *
     * @param keyValueCollection
     *            String collection
     *
     * @throws IllegalArgumentException
     *             if the array has a odd number of values
     * @throws ReferenceError
     *             if the referenced Parameter name is unknown
     *
     * @return immutable RunParameters object
     */
    RunParameters setFrom(final Collection<String> keyValueCollection);

    /**
     * Set the actual Parameters from a String Map.
     *
     * @param map
     *            String Map
     *
     * @throws ReferenceError
     *             if the referenced Parameter name is unknown
     *
     * @return immutable RunParameters object
     */
    RunParameters setFrom(final Map<String, String> map);

    /**
     * Return the parameters as Map
     *
     * @return immutable String Map
     */
    Map<String, String> map();
}
