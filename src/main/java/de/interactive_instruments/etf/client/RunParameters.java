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

    /**
     * The names of the parameters that the client must set
     *
     * @return immutable String Set
     */
    Set<String> required();

    /**
     * The client creates its own label for the test run, which is composed
     * of the prefix etf-client, a session ID and a counter for the test
     * runs. Example:
     *
     * <code>
     *     ETF-client 6bb13cdd-6e05-4731-95bf-ba93188b74f9 run 6
     * </code>.
     *
     * A suffix can be appended to the generated label. A hyphen is
     * automatically added when you set it. It must not be longer than
     * 70 characters. Example:
     *
     * <code>
     *     ETF-client 6bb13cdd-6e05-4731-95bf-ba93188b74f9 run 6 - My Test Run
     * </code>.
     *
     * @param testRunLabelSuffix the suffix that will be appended to the
     *                           generated label.
     *
     * @throws IllegalArgumentException
     *             if the String is null/empty or longer than 75 characters
     *
     * @return immutable RunParameters object
     */
    RunParameters labelSuffix(final String testRunLabelSuffix);
}
