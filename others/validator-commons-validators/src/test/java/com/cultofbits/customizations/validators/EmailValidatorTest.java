package com.cultofbits.customizations.validators;

import com.cultofbits.customizations.utils.DefinitionBuilder;
import com.cultofbits.customizations.utils.FieldDefinitionBuilder;
import com.cultofbits.customizations.utils.InstanceBuilder;
import com.cultofbits.customizations.validators.impl.Action;
import com.cultofbits.recordm.core.model.Definition;
import com.cultofbits.recordm.core.model.Instance;
import org.testng.annotations.Test;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

public class EmailValidatorTest {

    private final CommonsValidatorService validator = new CommonsValidatorService();

    @Test
    public void pass_validation_if_email_is_valid() {

        Definition definition = DefinitionBuilder.aDefinition()
            .fieldDefinitions(FieldDefinitionBuilder.aFieldDefinition().name("Email").description("$commons.validate(email)"))
            .build();

        Instance instance = InstanceBuilder.anInstance(definition)
            .fieldValue("Email", "test@cultofbits.com")
            .build();

        assertTrue(validator.validateInstanceFields(instance.getFields(), Action.ADD).isEmpty());
    }

    @Test
    public void pass_validation_if_email_is_null() {

        Definition definition = DefinitionBuilder.aDefinition()
            .fieldDefinitions(FieldDefinitionBuilder.aFieldDefinition().name("Email").description("$commons.validate(email)"))
            .build();

        Instance instance = InstanceBuilder.anInstance(definition)
            .build();

        assertTrue(validator.validateInstanceFields(instance.getFields(), Action.ADD).isEmpty());
    }

    @Test
    public void fail_validation_if_email_is_invalid() {

        Definition definition = DefinitionBuilder.aDefinition()
            .fieldDefinitions(FieldDefinitionBuilder.aFieldDefinition().name("Email").description("$commons.validate(email)"))
            .build();

        Instance instance = InstanceBuilder.anInstance(definition)
            .fieldValue("Email", "3.x")
            .build();

        assertFalse(validator.validateInstanceFields(instance.getFields(), Action.ADD).isEmpty());
    }
}
