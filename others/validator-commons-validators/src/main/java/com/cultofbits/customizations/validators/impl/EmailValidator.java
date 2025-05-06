package com.cultofbits.customizations.validators.impl;

import com.cultofbits.recordm.core.model.InstanceField;
import com.cultofbits.recordm.customvalidators.api.ValidationError;

import java.util.Collections;
import java.util.List;

import static com.cultofbits.recordm.customvalidators.api.LocalizedValidationError.localized;

public class EmailValidator implements CommonValidator {

    public boolean supports(String name) {
        return "email".equals(name.trim());
    }

    public List<ValidationError> validateOnCreate(InstanceField field, String validationConfiguration) {
        return getValidationErrors(field);
    }

    @Override
    public List<ValidationError> validateOnUpdate(InstanceField field, InstanceField persistedField, String validationConfiguration) {
        return getValidationErrors(field);
    }

    private static List<ValidationError> getValidationErrors(InstanceField field) {
        if (!org.apache.commons.validator.routines.EmailValidator.getInstance().isValid(field.getValue())) {
            return Collections.singletonList(localized(field, "commons-validators", "email.invalid-format"));
        }

        return Collections.emptyList();
    }
}
