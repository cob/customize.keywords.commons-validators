package com.cultofbits.customizations.validators.impl;

import com.cultofbits.recordm.core.model.InstanceField;
import com.cultofbits.recordm.customvalidators.api.ValidationError;

import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NoUpdateValidator implements CommonValidator {

    public static final Pattern VALIDATION_EXPRESSION = Pattern.compile("noUpdate\\(([^)].*)\\)");

    @Override
    public boolean supports(String name) {
        return name != null && name.startsWith("noUpdate");
    }

    @Override
    public List<ValidationError> validateOnCreate(InstanceField instanceField, String valExpr) {
        return Collections.emptyList();
    }

    @Override
    public List<ValidationError> validateOnUpdate(InstanceField updatedField, InstanceField persistedField, String valExpr) {
        Matcher matcher = VALIDATION_EXPRESSION.matcher(valExpr);
        String[] allowedGroups = (matcher.matches() ? matcher.group(1) : "").split(",");

        return Collections.emptyList();
    }
}
