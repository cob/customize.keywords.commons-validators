package com.cultofbits.customizations.validators.impl;

import com.cultofbits.customizations.utils.DefinitionBuilder;
import com.cultofbits.customizations.utils.FieldDefinitionBuilder;
import com.cultofbits.customizations.utils.InstanceBuilder;
import com.cultofbits.customizations.validators.CommonsValidatorService;
import com.cultofbits.recordm.core.model.Definition;
import com.cultofbits.recordm.core.model.Instance;
import com.cultofbits.recordm.persistence.InstanceRepository;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.Matchers.eq;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

public class RegexVaidatorTest {

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
    public void no_errors_when_matching() {

        Definition definition = DefinitionBuilder.aDefinition()
            .fieldDefinitions(
                FieldDefinitionBuilder.aFieldDefinition().name("value").description("$commons.validate('regex(^a|b|c$)')"))
            .build();

        Instance instance = InstanceBuilder.anInstance(definition)
            .fieldValue("value", "a")
            .build();

        assertTrue(validator.validateInstanceFields(instance.getFields(), instance, Action.ADD).isEmpty());
    }

    /**
     * Testing with ' wrapping the regex
     */
    @Test
    public void fail_when_not_matching() {

        Definition definition = DefinitionBuilder.aDefinition()
                .fieldDefinitions(
                        FieldDefinitionBuilder.aFieldDefinition().name("IP").description("$commons.validate('regex(^((25[0-5]|(2[0-4]|1\\d|[1-9]|)\\d)\\.?\\b){4}$)')"))
                .build();

        Instance instance = InstanceBuilder.anInstance(definition)
                .fieldValue("IP", "invalid ip")
                .build();

        assertFalse(validator.validateInstanceFields(instance.getFields(), instance, Action.ADD).isEmpty());
    }

    /**
     * Testing with " wrapping the regex
     */
    @Test
    public void fail_when_not_matching_with_quotation_marks() {

        Definition definition = DefinitionBuilder.aDefinition()
                .fieldDefinitions(
                        FieldDefinitionBuilder.aFieldDefinition().name("IP").description("$commons.validate(\"regex(^((25[0-5]|(2[0-4]|1\\d|[1-9]|)\\d)\\.?\\b){4}$)\")"))
                .build();

        Instance instance = InstanceBuilder.anInstance(definition)
                .fieldValue("IP", "invalid ip")
                .build();

        assertFalse(validator.validateInstanceFields(instance.getFields(), instance, Action.ADD).isEmpty());
    }

}
