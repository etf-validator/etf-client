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

import java.util.concurrent.ExecutorService;

import de.interactive_instruments.etf.client.*;

/**
 * @author Jon Herrmann ( herrmann aT interactive-instruments doT de )
 */
public class TrtExecutionContext {
    private Iterable<ExecutableTestSuite> allExecutableTestSuites;
    final InstanceCtx instanceCtx;
    private final ExecutorService executor;

    public TrtExecutionContext(final InstanceCtx ctx, final ExecutorService executor,
            final EtsCollection allExecutableTestSuites) {
        this.instanceCtx = ctx;
        this.executor = executor;
        this.allExecutableTestSuites = allExecutableTestSuites;
    }

    public void injectExecutableTestSuites(final Iterable<ExecutableTestSuite> allExecutableTestSuites) {
        this.allExecutableTestSuites = allExecutableTestSuites;
    }

    TestRun start(final TestRunTemplate testRunTemplate, final TestObject testObject,
            final TestRunObserver testRunObserver, final RunParameters parameters) throws RemoteInvocationException {
        return TestRunCmd.start(
                this.instanceCtx,
                this.executor,
                testRunTemplate,
                this.allExecutableTestSuites,
                testObject, testRunObserver, parameters);
    }
}
