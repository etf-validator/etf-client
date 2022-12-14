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

import org.json.JSONArray;
import org.json.JSONObject;

import de.interactive_instruments.etf.client.EtfCollection;
import de.interactive_instruments.etf.client.RemoteInvocationException;
import de.interactive_instruments.etf.client.Tag;

/**
 * @author Jon Herrmann ( herrmann aT interactive-instruments doT de )
 */
class TagCollectionCmd {

    private final static String PATH = "/Tags";

    private static class DefaultTag extends AbstractMetadata implements Tag {
        private final int priority;

        public DefaultTag(final JSONObject jsonObject) {
            super(jsonObject);
            this.priority = jsonObject.getInt("priority");
        }

        @Override
        public int priority() {
            return this.priority;
        }
    }

    private static class TagCollection extends AbstractEtfCollection<Tag> {

        TagCollection(final InstanceCtx ctx, final JSONArray jsonArray) {
            super(ctx, jsonArray);
        }

        @Override
        Tag doPrepare(final JSONObject jsonObject) {
            return new DefaultTag(jsonObject);
        }
    }

    private final JsonGetRequest apiCall;
    private final InstanceCtx ctx;
    private TagCollection cachedCollection;

    TagCollectionCmd(final InstanceCtx ctx) {
        this.ctx = ctx;
        this.apiCall = new JsonGetRequest(URI.create(ctx.baseUrl.toString() + PATH), ctx);
    }

    synchronized EtfCollection<Tag> query() throws RemoteInvocationException {
        if (apiCall.upToDate()) {
            return cachedCollection;
        } else {
            final JSONArray result = apiCall.query().getJSONObject("EtfItemCollection").getJSONObject("tags")
                    .getJSONArray("Tag");
            cachedCollection = new TagCollection(this.ctx, result);
        }
        return cachedCollection;
    }
}
