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
package de.interactive_instruments.etf.client.internal;

import java.util.*;

import org.json.JSONArray;
import org.json.JSONObject;

import de.interactive_instruments.etf.client.EtfCollection;
import de.interactive_instruments.etf.client.ItemMetadata;

/**
 * @author Jon Herrmann ( herrmann aT interactive-instruments doT de )
 */
abstract class AbstractEtfCollection<V extends ItemMetadata> implements EtfCollection<V> {

    protected final Map<String, V> items;
    protected final InstanceCtx ctx;

    protected AbstractEtfCollection(final InstanceCtx ctx, final JSONArray jsonArray) {
        this.ctx = ctx;
        items = new HashMap<>();
        initChildren(jsonArray);
    }

    protected AbstractEtfCollection(final Collection<JSONObject> jsonObjects, final InstanceCtx ctx) {
        this.ctx = ctx;
        items = new HashMap<>();
        initChildren(jsonObjects);
    }

    protected AbstractEtfCollection(final InstanceCtx ctx, final Collection<V> items) {
        this.ctx = ctx;
        this.items = new HashMap<>();
        for (final V item : items) {
            this.items.put(item.eid(), item);
        }
    }

    protected AbstractEtfCollection(final InstanceCtx ctx) {
        this.ctx = ctx;
        items = new HashMap<>();
    }

    protected void initChildren(final JSONArray jsonArray) {
        for (final Object o : jsonArray) {
            final JSONObject jsonObject = (JSONObject) o;
            final V preparedObject = doPrepare(jsonObject);
            items.put(preparedObject.eid(), preparedObject);
        }
    }

    protected void initChildren(final Collection<JSONObject> jsonObjects) {
        for (final JSONObject jsonObject : jsonObjects) {
            final V preparedObject = doPrepare(jsonObject);
            items.put(preparedObject.eid(), preparedObject);
        }
    }

    abstract V doPrepare(final JSONObject object);

    @Override
    final public Collection<? extends ItemMetadata> metadata() {
        return items.values();
    }

    @Override
    final public Optional<V> itemById(final String eid) {
        return Optional.ofNullable(items.get(eid));
    }

    @Override
    public Optional<V> itemByLabel(final String label) {
        for (final V item : items.values()) {
            if (item.label().equals(label)) {
                return Optional.of(item);
            }
        }
        return Optional.empty();
    }

    @Override
    public Iterator<V> iterator() {
        return items.values().iterator();
    }
}
