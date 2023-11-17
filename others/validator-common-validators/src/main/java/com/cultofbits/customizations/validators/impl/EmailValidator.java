package com.cultofbits.customizations.validators.impl;

import com.cultofbits.recordm.core.model.InstanceField;
import com.cultofbits.recordm.customvalidators.api.ValidationError;

import java.util.Collections;
import java.util.List;

import static com.cultofbits.recordm.customvalidators.api.LocalizedValidationError.*;

public class EmailValidator implements CommonValidator {

    public boolean supports(String name) {
        return "email".equals(name.trim());
    }

    public List<ValidationError> validate(InstanceField field, Action action, String validationConfiguration) {
        if (!org.apache.commons.validator.routines.EmailValidator.getInstance().isValid(field.getValue())) {
            return Collections.singletonList(localized(field, "common-validators", "email.invalid-format"));
        }

        return Collections.emptyList();
    }
}
