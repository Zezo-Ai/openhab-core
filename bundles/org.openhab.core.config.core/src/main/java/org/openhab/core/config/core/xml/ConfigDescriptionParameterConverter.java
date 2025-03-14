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
package org.openhab.core.config.core.xml;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.core.config.core.ConfigDescriptionParameter;
import org.openhab.core.config.core.ConfigDescriptionParameter.Type;
import org.openhab.core.config.core.ConfigDescriptionParameterBuilder;
import org.openhab.core.config.core.FilterCriteria;
import org.openhab.core.config.core.ParameterOption;
import org.openhab.core.config.core.xml.util.ConverterAttributeMapValidator;
import org.openhab.core.config.core.xml.util.ConverterValueMap;
import org.openhab.core.config.core.xml.util.GenericUnmarshaller;
import org.openhab.core.config.core.xml.util.NodeValue;

import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;

/**
 * The {@link ConfigDescriptionParameterConverter} is a concrete implementation
 * of the {@code XStream} {@link Converter} interface used to convert config
 * description parameters information within an XML document into a {@link ConfigDescriptionParameter} object.
 * <p>
 * This converter converts {@code parameter} XML tags.
 *
 * @author Michael Grammling - Initial contribution
 * @author Alex Tugarev - Extended for options and filter criteria
 * @author Chris Jackson - Modified to use config parameter builder. Added
 *         parameters.
 * @author Thomas Höfer - Added unit
 */
@NonNullByDefault
public class ConfigDescriptionParameterConverter extends GenericUnmarshaller<ConfigDescriptionParameter> {

    private ConverterAttributeMapValidator attributeMapValidator;

    public ConfigDescriptionParameterConverter() {
        super(ConfigDescriptionParameter.class);

        this.attributeMapValidator = new ConverterAttributeMapValidator(
                new String[][] { { "name", "true" }, { "type", "true" }, { "min", "false" }, { "max", "false" },
                        { "step", "false" }, { "pattern", "false" }, { "required", "false" }, { "readOnly", "false" },
                        { "multiple", "false" }, { "groupName", "false" }, { "unit", "false" } });
    }

    private @Nullable Type toType(@Nullable String xmlType) {
        if (xmlType != null) {
            return Type.valueOf(xmlType.toUpperCase());
        }

        return null;
    }

    private @Nullable BigDecimal toNumber(@Nullable String value) {
        try {
            if (value != null) {
                return new BigDecimal(value);
            }
        } catch (NumberFormatException e) {
            throw new ConversionException("The value '" + value + "' could not be converted to a decimal number.", e);
        }
        return null;
    }

    private @Nullable Boolean toBoolean(@Nullable String val) {
        if (val == null) {
            return null;
        }
        return Boolean.valueOf(val);
    }

    private Boolean falseIfNull(@Nullable Boolean b) {
        return b != null && b;
    }

    @Override
    public @Nullable Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
        ConfigDescriptionParameter configDescriptionParam;

        // read attributes
        Map<String, String> attributes = this.attributeMapValidator.readValidatedAttributes(reader);
        String name = attributes.get("name");
        Type type = toType(attributes.get("type"));
        BigDecimal min = toNumber(attributes.get("min"));
        BigDecimal max = toNumber(attributes.get("max"));
        BigDecimal step = toNumber(attributes.get("step"));
        String patternString = attributes.get("pattern");
        Boolean required = toBoolean(attributes.get("required"));
        Boolean readOnly = falseIfNull(toBoolean(attributes.get("readOnly")));
        Boolean multiple = falseIfNull(toBoolean(attributes.get("multiple")));
        String groupName = attributes.get("groupName");
        String unit = attributes.get("unit");

        // read values
        ConverterValueMap valueMap = new ConverterValueMap(reader, context);
        String parameterContext = valueMap.getString("context");
        if (required == null) {
            // fallback to deprecated "required" element
            required = valueMap.getBoolean("required", false);
        }
        String defaultValue = valueMap.getString("default");
        String label = valueMap.getString("label");
        String description = valueMap.getString("description");

        Boolean advanced = valueMap.getBoolean("advanced", false);
        Boolean verify = valueMap.getBoolean("verify", false);
        Boolean limitToOptions = valueMap.getBoolean("limitToOptions", true);
        Integer multipleLimit = valueMap.getInteger("multipleLimit");
        String unitLabel = null;
        if (unit == null) {
            unitLabel = valueMap.getString("unitLabel");
        }

        // read options and filter criteria
        List<ParameterOption> options = readParameterOptions(valueMap.getObject("options"));
        @SuppressWarnings("unchecked")
        List<FilterCriteria> filterCriteria = (List<FilterCriteria>) valueMap.getObject("filter");

        // create object
        configDescriptionParam = ConfigDescriptionParameterBuilder.create(name, type).withMinimum(min).withMaximum(max)
                .withStepSize(step).withPattern(patternString).withRequired(required).withReadOnly(readOnly)
                .withMultiple(multiple).withContext(parameterContext).withDefault(defaultValue).withLabel(label)
                .withDescription(description).withOptions(options).withFilterCriteria(filterCriteria)
                .withGroupName(groupName).withAdvanced(advanced).withVerify(verify).withLimitToOptions(limitToOptions)
                .withMultipleLimit(multipleLimit).withUnit(unit).withUnitLabel(unitLabel).build();

        return configDescriptionParam;
    }

    private @Nullable List<ParameterOption> readParameterOptions(@Nullable Object rawNodeValueList) {
        if (rawNodeValueList instanceof List<?> list) {
            List<ParameterOption> result = new ArrayList<>();
            for (Object object : list) {
                if (object instanceof NodeValue nodeValue) {
                    String value = nodeValue.getAttributes().get("value");
                    String label = nodeValue.getValue().toString();
                    result.add(new ParameterOption(value, label));
                }
            }
            return result;
        }
        return null;
    }
}
