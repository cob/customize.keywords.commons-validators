package com.cultofbits.customizations.validators;

import com.cultofbits.customizations.utils.DefinitionBuilder;
import com.cultofbits.customizations.utils.FieldDefinitionBuilder;
import com.cultofbits.customizations.utils.InstanceBuilder;
import com.cultofbits.recordm.core.model.Definition;
import com.cultofbits.recordm.core.model.Instance;
import org.testng.annotations.Test;

import static org.testng.Assert.assertTrue;

public class EmaillValidatorTest {

    private final EmailValidator validator = new EmailValidator();

    @Test
    public void pass_validation_if_email_is_valid() {

        Definition definition = DefinitionBuilder.aDefinition()
                .fieldDefinitions(FieldDefinitionBuilder.aFieldDefinition().name("Email").description("$commonValidators.email"))
                .build();

        Instance instance = InstanceBuilder.anInstance(definition)
                .fieldValue("Email", "test@cultofbits.com")
                .build();

        assertTrue(validator.validateInstanceFields(instance.getFields()).isEmpty());
    }

    @Test
    public void pass_validation_if_email_is_null() {

        Definition definition = DefinitionBuilder.aDefinition()
                .fieldDefinitions(FieldDefinitionBuilder.aFieldDefinition().name("Email").description("$commonValidators.email"))
                .build();

        Instance instance = InstanceBuilder.anInstance(definition)
                .build();

        assertTrue(validator.validateInstanceFields(instance.getFields()).isEmpty());
    }

    @Test
    public void fail_validation_if_email_is_invalid() {

        Definition definition = DefinitionBuilder.aDefinition()
                .fieldDefinitions(FieldDefinitionBuilder.aFieldDefinition().name("Email").description("$commonValidators.email"))
                .build();

        Instance instance = InstanceBuilder.anInstance(definition)
                .build();

        assertTrue(validator.validateInstanceFields(instance.getFields()).isEmpty());
    }
}
