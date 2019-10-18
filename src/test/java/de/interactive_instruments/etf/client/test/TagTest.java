/*
 * Copyright 2010-2019 interactive instruments GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.interactive_instruments.etf.client.test;

import static org.junit.jupiter.api.Assertions.*;

import de.interactive_instruments.etf.client.EtfCollection;
import de.interactive_instruments.etf.client.EtfEndpoint;
import de.interactive_instruments.etf.client.RemoteInvocationException;
import de.interactive_instruments.etf.client.Tag;
import org.junit.jupiter.api.Test;

/**
 * @author Jon Herrmann ( herrmann aT interactive-instruments doT de )
 */
public class TagTest {

    public final static String METADATA_TAG_ID = "EIDc6567beb-fc33-4f2e-865d-0c3ee5b3d1ae";
    private static final String METADATA_TAG_LABEL = "Metadata (TG version 2.0) - BETA";

    @Test
    void findTagInCollection() throws RemoteInvocationException {
        final EtfEndpoint etfEndpoint = Constants.ETF_ENDPOINT;

        // find tag in collection
        boolean tagFound = false;
        final EtfCollection<Tag> tagCollection = etfEndpoint.tags();
        for (final Tag tag : tagCollection) {
            assertNotNull(tag.eid());
            assertNotNull(tag.label());
            assertNotNull(tag.description());
            assertTrue(tag.priority() > 0);

            if (METADATA_TAG_LABEL.equals(tag.label())) {
                tagFound = true;
                assertEquals(METADATA_TAG_ID, tag.eid());
            }
        }
        assertTrue(tagFound);
    }

    @Test
    void findTagById() throws RemoteInvocationException {
        final EtfEndpoint etfEndpoint = Constants.ETF_ENDPOINT;

        final EtfCollection<Tag> tagCollection = etfEndpoint.tags();
        // get TAG by ID
        final Tag tag = etfEndpoint.tags().itemById(METADATA_TAG_ID).get();
        assertEquals(METADATA_TAG_ID, tag.eid());
        assertEquals(METADATA_TAG_LABEL, tag.label());

        // Ensure that both requested objects reference the same tag
        assertEquals(tagCollection.itemById(METADATA_TAG_ID).get(), tag);

        boolean tagFound = false;
        for (final Tag t : tagCollection) {
            if (t.equals(tag)) {
                tagFound = true;
                assertEquals(METADATA_TAG_ID, t.eid());
                assertEquals(METADATA_TAG_LABEL, t.label());
            }
        }
        assertTrue(tagFound);
    }

    @Test
    void tagNotFound() throws RemoteInvocationException {
        final EtfEndpoint etfEndpoint = Constants.ETF_ENDPOINT;
        assertTrue(etfEndpoint.tags().itemById("").isEmpty());
    }

    @Test
    void caching() throws RemoteInvocationException {
        final EtfEndpoint etfEndpoint = Constants.ETF_ENDPOINT;
        assertEquals(etfEndpoint.tags().itemById(METADATA_TAG_ID), etfEndpoint.tags().itemById(METADATA_TAG_ID));
    }
}
