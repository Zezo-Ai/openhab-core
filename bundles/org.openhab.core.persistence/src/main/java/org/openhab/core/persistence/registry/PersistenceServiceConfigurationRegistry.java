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
package org.openhab.core.persistence.registry;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.openhab.core.common.registry.Registry;
import org.openhab.core.persistence.internal.PersistenceManagerImpl;

/**
 * The {@link PersistenceServiceConfigurationRegistry} is the central place to store persistence service configurations.
 * Configurations are registered through {@link PersistenceServiceConfigurationProvider}.
 * Because the {@link PersistenceManagerImpl} implementation needs to listen to
 * different registries, the {@link PersistenceServiceConfigurationRegistryChangeListener} can be used to add listeners
 * to this registry.
 *
 * @author Jan N. Klug - Initial contribution
 */
@NonNullByDefault
public interface PersistenceServiceConfigurationRegistry extends Registry<PersistenceServiceConfiguration, String> {
    void addRegistryChangeListener(PersistenceServiceConfigurationRegistryChangeListener listener);

    void removeRegistryChangeListener(PersistenceServiceConfigurationRegistryChangeListener listener);
}
