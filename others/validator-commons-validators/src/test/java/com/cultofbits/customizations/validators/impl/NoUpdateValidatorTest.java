package com.cultofbits.customizations.validators.impl;

import com.cultofbits.customizations.utils.DefinitionBuilder;
import com.cultofbits.customizations.utils.FieldDefinitionBuilder;
import com.cultofbits.customizations.utils.InstanceBuilder;
import com.cultofbits.customizations.validators.CommonsValidatorService;
import com.cultofbits.genesis.security.PermissionContext;
import com.cultofbits.genesis.security.model.UserData;
import com.cultofbits.recordm.core.model.Definition;
import com.cultofbits.recordm.core.model.Instance;
import com.cultofbits.recordm.core.model.InstanceField;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertTrue;

public class NoUpdateValidatorTest {

    private CommonsValidatorService validator;

    @BeforeMethod
    public void setUp() {
        validator = new CommonsValidatorService();
    }

    @AfterMethod
    public void tearDown() {
        PermissionContext.clearUserData();
    }

    @Test
    public void no_errors_on_create() {
        Definition definition = DefinitionBuilder.aDefinition()
            .fieldDefinitions(
                FieldDefinitionBuilder.aFieldDefinition().name("value").description("$commons.validate(noUpdate(Group1,Group2))"))
            .build();

        Instance instance = InstanceBuilder.anInstance(definition)
            .fieldValue("value", "a")
            .build();

        assertTrue(validator.validateInstanceFields(instance.getFields(), instance, Action.ADD).isEmpty());
    }

    @Test
    public void no_errors_if_value_unchanged() {
        Definition definition = DefinitionBuilder.aDefinition()
            .fieldDefinitions(
                FieldDefinitionBuilder.aFieldDefinition().name("value").description("$commons.validate(noUpdate(Group1,Group2))"))
            .build();

        Instance persistedInstance = InstanceBuilder.anInstance(definition)
            .fieldValue("value", "a")
            .build();

        InstanceField valueField = persistedInstance.getField("value");
        valueField.id = 1L;

        Instance updatedInstance = InstanceBuilder.anInstance(definition)
            .fieldValue("value", "a")
            .build();

        valueField = updatedInstance.getField("value");
        valueField.id = 1L;

        assertTrue(validator.validateInstanceFields(updatedInstance.getFields(), persistedInstance, Action.UPDATE).isEmpty());
    }

    @Test
    public void add_error_if_unauthorized_update() {
        Definition definition = DefinitionBuilder.aDefinition()
            .fieldDefinitions(
                FieldDefinitionBuilder.aFieldDefinition().name("value").description("$commons.validate(noUpdate(Group1,Group2))"))
            .build();

        Instance persistedInstance = InstanceBuilder.anInstance(definition)
            .fieldValue("value", "a")
            .build();

        InstanceField valueField = persistedInstance.getField("value");
        valueField.id = 1L;

        Instance updatedInstance = InstanceBuilder.anInstance(definition)
            .fieldValue("value", "b")
            .build();

        valueField = updatedInstance.getField("value");
        valueField.id = 1L;


        UserData userData = mock(UserData.class);
        when(userData.getGroups()).thenReturn(Collections.emptyList());
        PermissionContext.setUserData(userData);

        assertTrue(validator.validateInstanceFields(updatedInstance.getFields(), persistedInstance, Action.UPDATE).size() > 0);
    }

    @Test
    public void add_error_if_unauthorized_changed_and_no_groups_allowed() {
        Definition definition = DefinitionBuilder.aDefinition()
            .fieldDefinitions(
                FieldDefinitionBuilder.aFieldDefinition().name("value").description("$commons.validate(noUpdate)"))
            .build();

        Instance persistedInstance = InstanceBuilder.anInstance(definition)
            .fieldValue("value", "a")
            .build();

        InstanceField valueField = persistedInstance.getField("value");
        valueField.id = 1L;

        Instance updatedInstance = InstanceBuilder.anInstance(definition)
            .fieldValue("value", "b")
            .build();

        valueField = updatedInstance.getField("value");
        valueField.id = 1L;


        UserData userData = mock(UserData.class);
        when(userData.getGroups()).thenReturn(Collections.emptyList());
        PermissionContext.setUserData(userData);

        assertTrue(validator.validateInstanceFields(updatedInstance.getFields(), persistedInstance, Action.UPDATE).size() > 0);
    }

    @Test
    public void no_error_if_changed_is_user_belongs_to_one_of_configured_groups() {
        Definition definition = DefinitionBuilder.aDefinition()
            .fieldDefinitions(
                FieldDefinitionBuilder.aFieldDefinition().name("value").description("$commons.validate(noUpdate(Group1,Group2))"))
            .build();

        Instance persistedInstance = InstanceBuilder.anInstance(definition)
            .fieldValue("value", "a")
            .build();

        InstanceField valueField = persistedInstance.getField("value");
        valueField.id = 1L;

        Instance updatedInstance = InstanceBuilder.anInstance(definition)
            .fieldValue("value", "b")
            .build();

        valueField = updatedInstance.getField("value");
        valueField.id = 1L;


        UserData userData = mock(UserData.class);
        when(userData.getGroups()).thenReturn(Arrays.asList("Group1", "System"));
        PermissionContext.setUserData(userData);

        assertTrue(validator.validateInstanceFields(updatedInstance.getFields(), persistedInstance, Action.UPDATE).isEmpty());
    }

}