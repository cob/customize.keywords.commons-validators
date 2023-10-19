package com.cultofbits.customizations.validators;

import com.cultofbits.recordm.core.model.Instance;
import com.cultofbits.recordm.core.model.InstanceField;
import com.cultofbits.recordm.customvalidators.api.OnCreateValidator;
import com.cultofbits.recordm.customvalidators.api.OnUpdateValidator;
import com.cultofbits.recordm.customvalidators.api.ValidationError;
import com.cultofbits.recordm.persistence.InstanceRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static com.cultofbits.recordm.customvalidators.api.ValidationError.custom;

/**
 * Email validator that uses the same email validator when registering users in UserM.
 * This is important when using scripts in IntegrationM that create users in UserM
 */
public class EmailValidator implements OnCreateValidator, OnUpdateValidator {

    public static final String KEYWORD = "$commonValidators.email";

    @Override
    public void setInstanceRepository(InstanceRepository instanceRepository) {
        // ignore
    }

    @Override
    public Collection<ValidationError> onCreate(Instance instance) {
        return validateInstanceFields(instance.getRootFields());
    }

    @Override
    public Collection<ValidationError> onUpdate(Instance persistedInstance, Instance updatedInstance) {
        return validateInstanceFields(updatedInstance.getRootFields());
    }

    public Collection<ValidationError> validateInstanceFields(List<InstanceField> instanceFields) {
        List<ValidationError> errors = new ArrayList<>();

        for (InstanceField instanceField : instanceFields) {
            if ((!instanceField.isVisible() || instanceField.getValue() == null)
                && instanceField.children.isEmpty()) {
                continue;
            }

            if (instanceField.fieldDefinition.containsExtension(KEYWORD)) {
                if (instanceField.getValue() == null) {
                    return Collections.emptyList();
                }

                if (!org.apache.commons.validator.routines.EmailValidator.getInstance().isValid(instanceField.getValue())) {
                    errors.add(custom(instanceField, "common-validators", "email.invalid-format"));
                }
            }

            if (!instanceField.children.isEmpty()) {
                errors.addAll(validateInstanceFields(instanceField.children));
            }
        }

        return errors;
    }
}
