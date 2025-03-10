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
package org.openhab.core.io.rest;

import java.util.Locale;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.core.i18n.LocaleProvider;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link LocaleServiceImpl} provides helper method for working with locales in REST
 * resources.
 *
 * @author Dennis Nobel - Initial contribution
 * @author Markus Rathgeb - Use locale provider
 * @author Martin Herbst - Support of different language definition variants
 * @author Lyubomir Papazov - Add component annotation, rename the class to LocaleService and add method tryGetLocale
 */
@Component
@NonNullByDefault
public class LocaleServiceImpl implements LocaleService {

    private final Logger logger = LoggerFactory.getLogger(LocaleServiceImpl.class);

    private volatile @Nullable LocaleProvider localeProvider;

    @Override
    public Locale getLocale(@Nullable String acceptLanguageHttpHeader) {
        Locale locale = tryGetLocale();
        if (acceptLanguageHttpHeader != null) {
            int pos = acceptLanguageHttpHeader.indexOf(',');
            String[] split;
            if (pos > -1) {
                split = acceptLanguageHttpHeader.substring(0, pos).split("-");
            } else {
                split = acceptLanguageHttpHeader.split("-");
            }
            if (split.length == 2) {
                locale = Locale.of(split[0], split[1]);
            } else {
                locale = Locale.of(split[0]);
            }
        }
        return locale;
    }

    @Reference(policy = ReferencePolicy.DYNAMIC)
    protected void setLocaleProvider(LocaleProvider provider) {
        logger.debug("The localeProvider in {} has been set", LocaleServiceImpl.class.getSimpleName());
        localeProvider = provider;
    }

    protected void unsetLocaleProvider(LocaleProvider provider) {
        logger.debug("The localeProvider in {} has been unset", LocaleServiceImpl.class.getSimpleName());
        localeProvider = null;
    }

    /**
     * Returns the provided locale or the default one if the provider is null.
     *
     * @return the default locale
     */
    private Locale tryGetLocale() {
        final LocaleProvider provider = localeProvider;
        if (provider != null) {
            return provider.getLocale();
        } else {
            logger.error("There should ALWAYS be a locale provider available, as it is provided by the core.");
            return Locale.US;
        }
    }
}
