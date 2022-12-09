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

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import de.interactive_instruments.etf.client.EtfCollection;
import de.interactive_instruments.etf.client.EtfIllegalStateException;
import de.interactive_instruments.etf.client.RemoteInvocationException;

/**
 * @author Jon Herrmann ( herrmann aT interactive-instruments doT de )
 */
abstract class AbstractCollectionCmd {

    static EtfCollection<?> toCollection(final CompletableFuture futureCollection) throws RemoteInvocationException {
        final Object futureResult;
        try {
            futureResult = futureCollection.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new EtfIllegalStateException("Interrupted", e);
        }
        if (futureResult instanceof RemoteInvocationException) {
            throw (RemoteInvocationException) futureResult;
        }

        return (EtfCollection<?>) futureResult;
    }
}
