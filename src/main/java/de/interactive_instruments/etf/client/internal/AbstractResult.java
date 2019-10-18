/**
 * Copyright 2017-2019 European Union, interactive instruments GmbH
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
 *
 * This work was supported by the EU Interoperability Solutions for
 * European Public Administrations Programme (http://ec.europa.eu/isa)
 * through Action 1.17: A Reusable INSPIRE Reference Platform (ARE3NA).
 */
package de.interactive_instruments.etf.client.internal;

import java.net.URI;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import org.json.JSONArray;
import org.json.JSONObject;

import de.interactive_instruments.etf.client.ExecutableTestSuite;
import de.interactive_instruments.etf.client.RemoteInvocationException;
import de.interactive_instruments.etf.client.ResultStatus;
import de.interactive_instruments.etf.client.TestResult;

/**
 * @author Jon Herrmann ( herrmann aT interactive-instruments doT de )
 */
abstract class AbstractResult implements TestResult {

    private final String label;
    private final String description;
    private final ResultStatus status;
    private final LocalDateTime startDate;
    private final long duration;
    protected final ResultCtx resultCtx;

    static class ResultCtx {
        final InstanceCtx ctx;
        private final EtsImpl ets;
        final JSONObject jsonObj;

        ResultCtx(final InstanceCtx ctx, final ExecutableTestSuite ets) {
            this.ctx = ctx;
            this.ets = (EtsImpl) ets;
            this.jsonObj = null;
        }

        private ResultCtx(final ResultCtx other, final JSONObject jsonObj) {
            this.ctx = other.ctx;
            this.ets = other.ets;
            this.jsonObj = jsonObj;
        }

        ResultCtx newChild(final JSONObject childObject) {
            return new ResultCtx(this, childObject);
        }

        String translate(final JSONObject messageJson) {
            return ets.translate(ctx.locale, messageJson);
        }

        EidObjectMapping eidObjectMapping() {
            return ets.objectMapping();
        }
    }

    protected AbstractResult(final ResultCtx resultCtx) {
        this.resultCtx = resultCtx;
        final String eidRef = this.resultCtx.jsonObj.getJSONObject("resultedFrom").getString("ref");
        final JSONObject etsItem = this.resultCtx.eidObjectMapping().resolve(eidRef);
        this.label = etsItem.getString("label");
        this.description = etsItem.getString("description");
        this.status = ResultStatus.fromString(this.resultCtx.jsonObj.getString("status"));
        final DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZ");
        this.startDate = LocalDateTime.from(
                DateTimeFormatter.ISO_DATE_TIME.parse(
                        this.resultCtx.jsonObj.getString("startTimestamp")));
        this.duration = this.resultCtx.jsonObj.getLong("duration");
    }

    protected Collection<? extends TestResult> createChildren(final JSONObject jsonObj, final String name,
            final String childrenName) {
        final Object childrenJson = jsonObj.getJSONObject(name).get(childrenName);
        if (childrenJson instanceof JSONArray) {
            final JSONArray childrenArray = (JSONArray) childrenJson;
            final Collection<TestResult> children = new ArrayList<>(((JSONArray) childrenJson).length());
            for (final Object o : childrenArray) {
                children.add(doCreateChild((JSONObject) o));
            }
            return Collections.unmodifiableCollection(children);
        } else if (childrenJson instanceof JSONObject) {
            return Collections.singleton(doCreateChild((JSONObject) childrenJson));
        } else {
            return Collections.emptyList();
        }
    }

    private void addAttachment(final Map<String, String> attachmentMap, final JSONObject jsonObj) {
        final String attachmentLabel = jsonObj.getString("label");
        if (jsonObj.has("embeddedData")) {
            final String decodedStr = new String(Base64.getDecoder().decode(jsonObj.getString("embeddedData")));
            attachmentMap.put(attachmentLabel, decodedStr);
        } else if (jsonObj.has("referencedData")) {
            final String mimeType = jsonObj.getString("mimeType");
            final String ref = jsonObj.getJSONObject("referencedData").getString("href");
            if ("text/plain".equals(mimeType)) {
                final URI uri = URI.create(ref);
                try {
                    final String value = new SimpleGetRequest(uri, this.resultCtx.ctx).query();
                    attachmentMap.put(label, value);
                } catch (RemoteInvocationException e) {
                    throw new IllegalStateException(e);
                }
            } else {
                attachmentMap.put(label, ref);
            }
        }
    }

    protected Map<String, String> createAttachments(final JSONObject jsonObj) {
        if (jsonObj.has("attachments")) {
            final Object childrenJson = jsonObj.getJSONObject("attachments").get("attachment");
            if (childrenJson instanceof JSONArray) {
                final JSONArray childrenArray = (JSONArray) childrenJson;
                final Map<String, String> attachments = new HashMap<>();
                for (final Object o : childrenArray) {
                    final String ref = ((JSONObject) o).getString("ref");
                    final JSONObject attachmentJson = this.resultCtx.eidObjectMapping().resolve(ref);
                    addAttachment(attachments, attachmentJson);
                }
                return Collections.unmodifiableMap(attachments);
            } else if (childrenJson instanceof JSONObject) {
                final Map<String, String> attachments = new HashMap<>();
                addAttachment(attachments, (JSONObject) childrenJson);
                return Collections.unmodifiableMap(attachments);
            } else {
                return Collections.emptyMap();
            }
        } else {
            return Collections.emptyMap();
        }
    }

    final public String label() {
        return label;
    }

    @Override
    final public String description() {
        return description;
    }

    final public ResultStatus resultStatus() {
        return status;
    }

    abstract TestResult doCreateChild(final JSONObject child);

    @Override
    final public LocalDateTime startDate() {
        return startDate;
    }

    final public long duration() {
        return duration;
    }

    final static class TestResultIterator implements Iterator<TestResult> {

        private final Iterator<? extends TestResult> mainIt;
        private Iterator<? extends TestResult> currentItChild;
        private TestResult next;

        public TestResultIterator(final Iterator<? extends TestResult> it) {
            this.mainIt = it;
        }

        @Override
        public boolean hasNext() {
            this.next = null;
            if (this.currentItChild != null) {
                if (this.currentItChild.hasNext()) {
                    next = this.currentItChild.next();
                    return true;
                } else {
                    this.currentItChild = null;
                }
            }

            if (this.mainIt.hasNext()) {
                next = this.mainIt.next();
                this.currentItChild = new TestResultIterator(
                        ((AbstractResult) next).children().iterator());
                return true;
            }
            return false;
        }

        @Override
        public TestResult next() {
            if ("IGNORE".equals(this.next.label()) && hasNext()) {
                return next();
            }
            return this.next;
        }
    }

    abstract Iterable<? extends TestResult> children();

    @Override
    public final Iterator<TestResult> iterator() {
        return new TestResultIterator(children().iterator());
    }
}
