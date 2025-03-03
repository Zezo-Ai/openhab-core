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
package org.openhab.core.automation.module.script.internal;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.script.ScriptEngine;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.core.automation.module.script.ScriptEngineFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link ScriptEngineFactoryHelper} contains helper methods for handling script engines
 *
 * @author Jan N. Klug - Initial contribution
 */
@NonNullByDefault
public class ScriptEngineFactoryHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(ScriptEngineFactoryHelper.class);

    private ScriptEngineFactoryHelper() {
        // prevent instantiation of static utility class
    }

    public static Map.@Nullable Entry<String, String> getParameterOption(ScriptEngineFactory engineFactory) {
        List<String> scriptTypes = engineFactory.getScriptTypes();
        if (!scriptTypes.isEmpty()) {
            ScriptEngine scriptEngine = engineFactory.createScriptEngine(scriptTypes.getFirst());
            if (scriptEngine != null) {
                Map.Entry<String, String> parameterOption = Map.entry(getPreferredMimeType(engineFactory),
                        getLanguageName(scriptEngine.getFactory()));
                LOGGER.trace("ParameterOptions: {}", parameterOption);
                return parameterOption;
            } else {
                LOGGER.trace("setScriptEngineFactory: engine was null");
            }
        } else {
            LOGGER.trace("addScriptEngineFactory: scriptTypes was empty");
        }

        return null;
    }

    public static String getPreferredMimeType(ScriptEngineFactory factory) {
        List<String> scriptTypes = factory.getScriptTypes();
        if (scriptTypes.isEmpty()) {
            throw new IllegalStateException(
                    factory.getClass().getName() + " does not support any scriptTypes. Please report it as a bug.");
        }
        List<String> mimeTypes = new ArrayList<>(scriptTypes);
        mimeTypes.removeIf(mimeType -> !mimeType.contains("application") || "application/python".equals(mimeType));
        return mimeTypes.isEmpty() ? scriptTypes.getFirst() : mimeTypes.getFirst();
    }

    public static String getLanguageName(javax.script.ScriptEngineFactory factory) {
        return String.format("%s (%s)",
                factory.getLanguageName().substring(0, 1).toUpperCase() + factory.getLanguageName().substring(1),
                factory.getLanguageVersion());
    }

    public static Optional<String> getPreferredExtension(ScriptEngineFactory factory) {
        return factory.getScriptTypes().stream().filter(type -> !type.contains("/"))
                .min(Comparator.comparing(String::length));
    }
}
