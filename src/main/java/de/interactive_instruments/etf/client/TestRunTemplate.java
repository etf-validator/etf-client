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

import java.util.Collection;

/**
 * Test Run Template bundle multiple Executable Test Suites and are used to start a new test run with a specific test
 * object.
 *
 * @since 1.1
 *
 * @author Jon Herrmann ( herrmann aT interactive-instruments doT de )
 */
public interface TestRunTemplate extends TestRunExecutable, ItemMetadata {

    /**
     * The base Test Object Type supported by this Test Run Template
     *
     * @return the TestObjectBaseType
     */
    TestObjectBaseType supportedBaseType();

    /**
     * The Ids of the Executable TestSuites that are used by this Test Run Template
     *
     * @return collection of Strings
     */
    Collection<String> executableTestSuiteEids();
}
