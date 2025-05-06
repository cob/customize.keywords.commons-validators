package com.cultofbits.customizations.validators.impl;

import com.cultofbits.customizations.utils.DefinitionBuilder;
import com.cultofbits.customizations.utils.FieldDefinitionBuilder;
import com.cultofbits.customizations.utils.InstanceBuilder;
import com.cultofbits.customizations.validators.CommonsValidatorService;
import com.cultofbits.recordm.core.model.Definition;
import com.cultofbits.recordm.core.model.FieldDefinition;
import com.cultofbits.recordm.core.model.Instance;
import com.cultofbits.recordm.customvalidators.api.LocalizedValidationError;
import com.cultofbits.recordm.customvalidators.api.ValidationError;
import com.cultofbits.recordm.persistence.InstanceRepository;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import static java.util.Collections.singletonList;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;
import static org.testng.Assert.*;

public class UniqueValidatorTest {

    @Mock
    private InstanceRepository instanceRepository;

    private CommonsValidatorService validator;

    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        validator = new CommonsValidatorService();
        validator.setInstanceRepository(instanceRepository);
    }

    @Test
    public void no_errors_when_unique() {

        Definition definition = DefinitionBuilder.aDefinition()
            .fieldDefinitions(
                FieldDefinitionBuilder.aFieldDefinition().name("Type").description("$[Robot,User] $commons.validate(uniqueValue(showLink=true))"))
            .build();

        Instance instance = InstanceBuilder.anInstance(definition)
            .fieldValue("Type", "Robot")
            .build();

        when(instanceRepository.getInstanceIdsWithFieldsMatching(anyList(), eq("Robot"), eq(0), eq(2)))
            .thenReturn(Collections.emptyList());

        assertTrue(validator.validateInstanceFields(instance.getFields(), null, Action.ADD).isEmpty());
    }

    @Test
    public void new_instance_with_non_unique_value_will_return_errors() {

        Definition definition = DefinitionBuilder.aDefinition()
            .fieldDefinitions(
                FieldDefinitionBuilder.aFieldDefinition().name("Type").description("$[Robot,User] $commons.validate(uniqueValue(showLink=true))"))
            .build();

        Instance instance = InstanceBuilder.anInstance(definition)
            .fieldValue("Type", "Robot")
            .build();

        when(instanceRepository.getInstanceIdsWithFieldsMatching(anyList(), eq("Robot"), eq(0), eq(2)))
            .thenReturn(singletonList(1));

        assertFalse(validator.validateInstanceFields(instance.getFields(), null, Action.ADD).isEmpty());
    }

    @Test
    public void updating_instance_with_value_continues_to_be_unique() {

        Definition definition = DefinitionBuilder.aDefinition()
            .fieldDefinitions(
                FieldDefinitionBuilder.aFieldDefinition().name("Type").description("$[Robot,User] $commons.validate(uniqueValue(showLink=true))"))
            .build();

        Instance instance = InstanceBuilder.anInstance(definition)
            .id(2)
            .fieldValue("Type", "Robot")
            .build();

        when(instanceRepository.getInstanceIdsWithFieldsMatching(anyList(), eq("Robot"), eq(0), eq(2)))
            .thenReturn(singletonList(2));

        assertTrue(validator.validateInstanceFields(instance.getFields(), instance, Action.UPDATE).isEmpty());
    }

    @Test
    public void updating_instance_with_non_unique_value_will_return_errors() {

        Definition definition = DefinitionBuilder.aDefinition()
            .fieldDefinitions(
                FieldDefinitionBuilder.aFieldDefinition().name("Type").description("$[Robot,User] $commons.validate(uniqueValue(showLink=true))"))
            .build();

        Instance instance = InstanceBuilder.anInstance(definition)
            .id(3)
            .fieldValue("Type", "Robot")
            .build();

        when(instanceRepository.getInstanceIdsWithFieldsMatching(anyList(), eq("Robot"), eq(0), eq(2)))
            .thenReturn(singletonList(2));

        assertFalse(validator.validateInstanceFields(instance.getFields(), instance, Action.UPDATE).isEmpty());
    }

    @Test
    public void updating_instances_with_uniquevalue_in_children() {

        FieldDefinition idCardField =
            FieldDefinitionBuilder.aFieldDefinition().name("ID Number").description("$commons.validate(uniqueValue(showLink=true))").build();
        FieldDefinition nameField = FieldDefinitionBuilder.aFieldDefinition().name("Name").build();

        FieldDefinition parentField = FieldDefinitionBuilder.aFieldDefinition().name("ID Card")
            .description("$[Yes,No]")
            .childFields(idCardField, nameField)
            .build();

        Definition definition = DefinitionBuilder.aDefinition()
            .fieldDefinitions(parentField, idCardField, nameField)
            .build();

        Instance instance = InstanceBuilder.anInstance(definition)
            .id(3)
            .fieldValue("ID Card", "Yes")
            .fieldValue("ID Number", "111111111")
            .build();

        when(instanceRepository.getInstanceIdsWithFieldsMatching(eq(singletonList(idCardField.id)), eq("111111111"), eq(0), eq(2)))
            .thenReturn(singletonList(2));

        assertFalse(validator.validateInstanceFields(instance.getFields(), instance, Action.UPDATE).isEmpty());
    }

    @Test
    public void error_message_must_include_search_uri() {

        FieldDefinition idCardField =
            FieldDefinitionBuilder.aFieldDefinition().id(1000).name("ID Number").description("$commons.validate(uniqueValue(showLink=true))").build();
        FieldDefinition nameField = FieldDefinitionBuilder.aFieldDefinition().name("Name").build();

        FieldDefinition parentField = FieldDefinitionBuilder.aFieldDefinition().name("ID Card")
            .description("$[Yes,No]")
            .childFields(idCardField, nameField)
            .build();

        Definition definition = DefinitionBuilder.aDefinition()
            .fieldDefinitions(parentField, idCardField, nameField)
            .build();

        Instance instance = InstanceBuilder.anInstance(definition)
            .id(3)
            .fieldValue("ID Card", "Yes")
            .fieldValue("ID Number", "111111111")
            .build();

        when(instanceRepository.getInstanceIdsWithFieldsMatching(eq(singletonList(idCardField.id)), eq("111111111"), eq(0), eq(2)))
            .thenReturn(singletonList(2));

        Collection<ValidationError> validationErrors =
            validator.validateInstanceFields(instance.getFields(), instance, Action.UPDATE);

        LocalizedValidationError fieldError = (LocalizedValidationError) (new ArrayList<>(validationErrors).get(0));
        assertEquals(fieldError.getL10nKey(), "uniqueValue.not-unique-with-query");
        assertEquals(fieldError.getL10nArgs().get("queryUri"), "/recordm/index.html#/definitions/" + instance.definition.id + "/q=id_number.raw:\"111111111\"");
    }

    @Test
    public void error_message_must_not_include_search_uri() {

        FieldDefinition idCardField =
            FieldDefinitionBuilder.aFieldDefinition().id(1000).name("ID Number").description("$commons.validate(uniqueValue(showLink=false))").build();
        FieldDefinition nameField = FieldDefinitionBuilder.aFieldDefinition().name("Name").build();

        FieldDefinition parentField = FieldDefinitionBuilder.aFieldDefinition().name("ID Card")
            .description("$[Yes,No]")
            .childFields(idCardField, nameField)
            .build();

        Definition definition = DefinitionBuilder.aDefinition()
            .fieldDefinitions(parentField, idCardField, nameField)
            .build();

        Instance instance = InstanceBuilder.anInstance(definition)
            .id(3)
            .fieldValue("ID Card", "Yes")
            .fieldValue("ID Number", "111111111")
            .build();

        when(instanceRepository.getInstanceIdsWithFieldsMatching(eq(singletonList(idCardField.id)), eq("111111111"), eq(0), eq(2)))
            .thenReturn(singletonList(2));

        Collection<ValidationError> validationErrors =
            validator.validateInstanceFields(instance.getFields(), instance, Action.UPDATE);

        LocalizedValidationError fieldError = (LocalizedValidationError) (new ArrayList<>(validationErrors).get(0));
        assertEquals(fieldError.getL10nKey(), "uniqueValue.not-unique");
        assertNull(fieldError.getL10nArgs());
    }

    @Test
    public void default_to_add_Link_if_missing_showLink() {
        FieldDefinition idCardField =
            FieldDefinitionBuilder.aFieldDefinition().id(1000).name("ID Number").description("$commons.validate(uniqueValue(showLink))").build();
        FieldDefinition nameField = FieldDefinitionBuilder.aFieldDefinition().name("Name").build();

        FieldDefinition parentField = FieldDefinitionBuilder.aFieldDefinition().name("ID Card")
            .description("$[Yes,No]")
            .childFields(idCardField, nameField)
            .build();

        Definition definition = DefinitionBuilder.aDefinition()
            .fieldDefinitions(parentField, idCardField, nameField)
            .build();

        Instance instance = InstanceBuilder.anInstance(definition)
            .id(3)
            .fieldValue("ID Card", "Yes")
            .fieldValue("ID Number", "111111111")
            .build();

        when(instanceRepository.getInstanceIdsWithFieldsMatching(eq(singletonList(idCardField.id)), eq("111111111"), eq(0), eq(2)))
            .thenReturn(singletonList(2));

        Collection<ValidationError> validationErrors =
            validator.validateInstanceFields(instance.getFields(), instance, Action.UPDATE);

        LocalizedValidationError fieldError = (LocalizedValidationError) (new ArrayList<>(validationErrors).get(0));
        assertEquals(fieldError.getL10nKey(), "uniqueValue.not-unique-with-query");
    }

}
