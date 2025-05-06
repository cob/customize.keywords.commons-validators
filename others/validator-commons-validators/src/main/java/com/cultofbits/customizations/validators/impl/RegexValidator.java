package com.cultofbits.customizations.validators.impl;

import com.cultofbits.recordm.core.model.InstanceField;
import com.cultofbits.recordm.customvalidators.api.ValidationError;

import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.cultofbits.recordm.customvalidators.api.LocalizedValidationError.localized;

public class RegexValidator implements CommonValidator {

    public static final Pattern VALIDATION_EXPRESSION = Pattern.compile("['\"]regex\\(([^)].*)\\)['\"]");

    @Override
    public boolean supports(String name) {
        return name != null && VALIDATION_EXPRESSION.matcher(name).matches();
    }

    @Override
    public List<ValidationError> validateOnCreate(InstanceField field, String valExpr) {
        return getValidationErrors(field, valExpr);
    }

    @Override
    public List<ValidationError> validateOnUpdate(InstanceField field, InstanceField persistedField, String valExpr) {
        return getValidationErrors(field, valExpr);
    }

    private static List<ValidationError> getValidationErrors(InstanceField field, String valExpr) {
        Matcher matcher = VALIDATION_EXPRESSION.matcher(valExpr);
        if (matcher.matches() && field.getValue() != null) {
            String regex = matcher.group(1);
            Pattern pattern = Pattern.compile(regex);

            if (!pattern.matcher(field.getValue()).matches()) {
                return Collections.singletonList(localized(field, "commons-validators", "pattern.invalid-format"));
            }
        }

        return Collections.emptyList();
    }
}
