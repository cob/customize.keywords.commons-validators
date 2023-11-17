package com.cultofbits.customizations.validators.impl;

import com.cultofbits.recordm.core.model.InstanceField;
import com.cultofbits.recordm.customvalidators.api.ValidationError;

import java.util.List;

public interface CommonValidator {

    boolean supports(String name);

    List<ValidationError> validate(InstanceField instanceField, Action action, String validationConfiguration);

}
