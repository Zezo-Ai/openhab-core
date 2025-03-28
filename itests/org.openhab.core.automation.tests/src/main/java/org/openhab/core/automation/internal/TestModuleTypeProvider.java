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
package org.openhab.core.automation.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.core.automation.type.ActionType;
import org.openhab.core.automation.type.ConditionType;
import org.openhab.core.automation.type.Input;
import org.openhab.core.automation.type.ModuleType;
import org.openhab.core.automation.type.ModuleTypeProvider;
import org.openhab.core.automation.type.Output;
import org.openhab.core.automation.type.TriggerType;
import org.openhab.core.common.registry.ProviderChangeListener;

/**
 * ModuleTypeManagerMockup is a ModuleTypeManager which returns mockup module types for the following predefined module
 * types: triggerTypeUID, conditionTypeUID and actionTypeUID. The types have preset tags of their input and outputs.
 *
 * @author Yordan Mihaylov - Initial contribution
 */
@NonNullByDefault
public class TestModuleTypeProvider implements ModuleTypeProvider {

    public static final String TRIGGER_TYPE = "triggerTypeUID";
    public static final String ACTION_TYPE = "actionTypeUID";
    public static final String CONDITION_TYPE = "conditionTypeUID";

    public TestModuleTypeProvider() {
        super();
    }

    private TriggerType createTriggerType() {
        List<Output> outputs = new ArrayList<>(3);
        outputs.add(createOutput("out1", Set.of("tagA")));
        outputs.add(createOutput("out2", Set.of("tagB", "tagC")));
        outputs.add(createOutput("out3", Set.of("tagA", "tagB", "tagC")));
        return new TriggerType(TRIGGER_TYPE, null, outputs);
    }

    private ConditionType createConditionType() {
        List<Input> inputs = new ArrayList<>(3);
        inputs.add(createInput("in0", Set.of("tagE"))); // no connection, missing condition tag
        inputs.add(createInput("in1", Set.of("tagA"))); // conflict in2 -> out1 or in2 -> out3
        inputs.add(createInput("in2", Set.of("tagA", "tagB")));
        // in2 -> out3
        return new ConditionType(CONDITION_TYPE, null, inputs);
    }

    private ActionType createActionType() {
        List<Input> inputs = new ArrayList<>(3);
        inputs.add(createInput("in3", Set.of("tagD"))); // conflict in3 -> out4 or in3 -> out5
        inputs.add(createInput("in4", Set.of("tagD", "tagE"))); // in4 -> out5
        inputs.add(createInput("in5", Set.of("tagA", "tagB", "tagC"))); // in5 -> out3
        inputs.add(createInput("in6", Set.of("tagA", "tagB"))); // conflict in6 has user defined connection

        List<Output> outputs = new ArrayList<>(3);
        outputs.add(createOutput("out4", Set.of("tagD")));
        outputs.add(createOutput("out5", Set.of("tagD", "tagE")));
        return new ActionType(ACTION_TYPE, null, inputs, outputs);
    }

    private Output createOutput(String name, Set<String> tags) {
        return new Output(name, String.class.getName(), null, null, tags, null, null);
    }

    private Input createInput(String name, Set<String> tags) {
        return new Input(name, String.class.getName(), null, null, tags, false, null, null);
    }

    @Override
    public void addProviderChangeListener(ProviderChangeListener<ModuleType> listener) {
    }

    @Override
    public Collection<ModuleType> getAll() {
        return Set.of(createTriggerType(), createConditionType(), createActionType());
    }

    @Override
    public void removeProviderChangeListener(ProviderChangeListener<ModuleType> listener) {
    }

    @Override
    public <T extends ModuleType> @Nullable T getModuleType(String uid, @Nullable Locale locale) {
        return null;
    }

    @Override
    public <T extends ModuleType> Collection<T> getModuleTypes(@Nullable Locale locale) {
        return List.of();
    }
}
