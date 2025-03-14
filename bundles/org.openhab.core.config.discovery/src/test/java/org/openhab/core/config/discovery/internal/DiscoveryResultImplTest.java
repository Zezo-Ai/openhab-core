/*
 * Copyright (c) 2010-2025 Contributors to the openHAB project
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.openhab.core.config.discovery.internal;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.junit.jupiter.api.Test;
import org.openhab.core.config.discovery.DiscoveryResultFlag;
import org.openhab.core.thing.ThingTypeUID;
import org.openhab.core.thing.ThingUID;

/**
 * The {@link DiscoveryResultImplTest} checks if any invalid input parameters
 * and the synchronization of {@link org.openhab.core.config.discovery.DiscoveryResult}s
 * work in a correct way.
 *
 * @author Michael Grammling - Initial contribution
 * @author Thomas Höfer - Added representation
 * @author Wouter Born - Migrate tests from Groovy to Java
 */
@NonNullByDefault
public class DiscoveryResultImplTest {

    private static final int DEFAULT_TTL = 60;

    @SuppressWarnings("deprecation")
    @Test
    public void testInvalidConstructorForThingType() {
        assertThrows(IllegalArgumentException.class,
                () -> new DiscoveryResultImpl(null, new ThingUID("aa"), null, null, null, null, DEFAULT_TTL));
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testInvalidConstructorForTTL() {
        ThingTypeUID thingTypeUID = new ThingTypeUID("bindingId", "thingType");
        assertThrows(IllegalArgumentException.class, () -> new DiscoveryResultImpl(thingTypeUID,
                new ThingUID(thingTypeUID, "thingId"), null, null, null, null, -2));
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testValidConstructor() {
        ThingTypeUID thingTypeUID = new ThingTypeUID("bindingId", "thingType");

        DiscoveryResultImpl discoveryResult = new DiscoveryResultImpl(thingTypeUID,
                new ThingUID(thingTypeUID, "thingId"), null, null, null, null, DEFAULT_TTL);

        assertEquals("bindingId:thingType", discoveryResult.getThingTypeUID().toString());
        assertEquals("bindingId:thingType:thingId", discoveryResult.getThingUID().toString());
        assertEquals("bindingId", discoveryResult.getBindingId());
        assertEquals("", discoveryResult.getLabel());
        assertEquals(DiscoveryResultFlag.NEW, discoveryResult.getFlag());

        assertNotNull(discoveryResult.getProperties(), "The properties must never be null!");
        assertNull(discoveryResult.getRepresentationProperty());
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testInvalidSynchronize() {
        ThingTypeUID thingTypeUID = new ThingTypeUID("bindingId", "thingType");

        Map<String, Object> discoveryResultSourceMap = Map.of("ipAddress", "127.0.0.1");

        DiscoveryResultImpl discoveryResult = new DiscoveryResultImpl(thingTypeUID,
                new ThingUID(thingTypeUID, "thingId"), null, discoveryResultSourceMap, "ipAddress", "TARGET",
                DEFAULT_TTL);
        discoveryResult.setFlag(DiscoveryResultFlag.IGNORED);

        discoveryResult.synchronize(null);

        assertEquals("127.0.0.1", discoveryResult.getProperties().get("ipAddress"));
        assertEquals("ipAddress", discoveryResult.getRepresentationProperty());
        assertEquals("TARGET", discoveryResult.getLabel());
        assertEquals(DiscoveryResultFlag.IGNORED, discoveryResult.getFlag());
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testIrrelevantSynchronize() {
        ThingTypeUID thingTypeUID = new ThingTypeUID("bindingId", "thingType");

        Map<String, Object> discoveryResultSourceMap = Map.of("ipAddress", "127.0.0.1");

        DiscoveryResultImpl discoveryResult = new DiscoveryResultImpl(thingTypeUID,
                new ThingUID(thingTypeUID, "thingId"), null, discoveryResultSourceMap, "ipAddress", "TARGET",
                DEFAULT_TTL);
        discoveryResult.setFlag(DiscoveryResultFlag.IGNORED);

        DiscoveryResultImpl discoveryResultSource = new DiscoveryResultImpl(thingTypeUID,
                new ThingUID(thingTypeUID, "anotherThingId"), null, null, null, null, DEFAULT_TTL);

        discoveryResult.synchronize(discoveryResultSource);

        assertEquals("127.0.0.1", discoveryResult.getProperties().get("ipAddress"));
        assertEquals("ipAddress", discoveryResult.getRepresentationProperty());
        assertEquals("TARGET", discoveryResult.getLabel());
        assertEquals(DiscoveryResultFlag.IGNORED, discoveryResult.getFlag());
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testSynchronize() {
        ThingTypeUID thingTypeUID = new ThingTypeUID("bindingId", "thingType");

        Map<String, Object> discoveryResultSourceMap = Map.of("ipAddress", "127.0.0.1");

        DiscoveryResultImpl discoveryResult = new DiscoveryResultImpl(thingTypeUID,
                new ThingUID(thingTypeUID, "thingId"), null, discoveryResultSourceMap, "ipAddress", "TARGET",
                DEFAULT_TTL);
        discoveryResult.setFlag(DiscoveryResultFlag.IGNORED);

        Map<String, Object> discoveryResultMap = Map.of( //
                "ipAddress", "192.168.178.1", //
                "macAddress", "AA:BB:CC:DD:EE:FF");

        DiscoveryResultImpl discoveryResultSource = new DiscoveryResultImpl(thingTypeUID,
                new ThingUID(thingTypeUID, "thingId"), null, discoveryResultMap, "macAddress", "SOURCE", DEFAULT_TTL);
        discoveryResultSource.setFlag(DiscoveryResultFlag.NEW);

        discoveryResult.synchronize(discoveryResultSource);

        assertEquals("192.168.178.1", discoveryResult.getProperties().get("ipAddress"));
        assertEquals("AA:BB:CC:DD:EE:FF", discoveryResult.getProperties().get("macAddress"));
        assertEquals("macAddress", discoveryResult.getRepresentationProperty());
        assertEquals("SOURCE", discoveryResult.getLabel());
        assertEquals(DiscoveryResultFlag.IGNORED, discoveryResult.getFlag());
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testThingTypeCompatibility() {
        ThingTypeUID thingTypeUID = new ThingTypeUID("bindingId", "thingType");
        DiscoveryResultImpl discoveryResult = new DiscoveryResultImpl(null, new ThingUID(thingTypeUID, "thingId"), null,
                null, "nothing", "label", DEFAULT_TTL);
        assertNotNull(discoveryResult.getThingTypeUID());
        assertEquals(discoveryResult.getThingTypeUID(), thingTypeUID);
    }
}
