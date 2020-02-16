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

import org.json.JSONObject;

import de.interactive_instruments.etf.client.ItemMetadata;

/**
 * @author Jon Herrmann ( herrmann aT interactive-instruments doT de )
 */
abstract class AbstractMetadata implements ItemMetadata {

    private final String eid;
    private final String label;
    private final String description;

    AbstractMetadata(final JSONObject jsonObject) {
        eid = jsonObject.getString("id");
        label = jsonObject.getString("label");
        description = jsonObject.has("description") ? jsonObject.getString("label") : "";
    }

    final public String eid() {
        return eid;
    }

    final public String label() {
        return label;
    }

    final public String description() {
        return description;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("{");
        sb.append("eid='").append(eid).append('\'');
        sb.append(", label='").append(label).append('\'');
        sb.append(", description='").append(description).append('\'');
        sb.append('}');
        return sb.toString();
    }

    protected static String eidFromUrl(final String url) {
        final int lastBackslash = url.lastIndexOf('/');
        final int p = url.lastIndexOf('.');
        final String eid;
        if (lastBackslash >= 0) {
            if (p > 0) {
                eid = url.substring(url.lastIndexOf('/') + 1, p);
            } else {
                eid = url.substring(url.lastIndexOf('/') + 1);
            }
        } else {
            eid = url;
        }
        if (eid.startsWith("EID")) {
            return eid;
        }
        return "EID" + eid;
    }
}
