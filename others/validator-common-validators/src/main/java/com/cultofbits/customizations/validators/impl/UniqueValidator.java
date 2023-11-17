
package com.cultofbits.customizations.validators.impl;

import com.cultofbits.recordm.core.model.InstanceField;
import com.cultofbits.recordm.customvalidators.api.ValidationError;
import com.cultofbits.recordm.persistence.InstanceRepository;

import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.cultofbits.recordm.customvalidators.api.LocalizedValidationError.localized;

public class UniqueValidator implements CommonValidator {

    public static final Pattern VALIDATION_EXPRESSION = Pattern.compile("uniqueValue\\(showLink=([^)].*)\\)");

    private final InstanceRepository instanceRepository;

    public UniqueValidator(InstanceRepository instanceRepository) {
        this.instanceRepository = instanceRepository;
    }

    public boolean supports(String name) {
        return name != null && name.startsWith("uniqueValue");
    }

    public List<ValidationError> validate(InstanceField field, Action action, String valExpr) {
        Matcher matcher = VALIDATION_EXPRESSION.matcher(valExpr);
        boolean addLink = matcher.matches() ? matcher.group(1).equals("true") : true;

        List<Integer> matchingInstances = instanceRepository.getInstanceIdsWithFieldsMatching(
            Collections.singletonList(field.fieldDefinition.id),
            field.getValue().trim(),
            0, 2);

        if (!matchingInstances.isEmpty()) {
            if (Action.ADD.equals(action)
                || (matchingInstances.size() >= 2 || !matchingInstances.get(0).equals(field.instance.id))) {
                return addLink
                       ? Collections.singletonList(localized(field, "common-validators", "uniqueValue.not-unique-with-query", Collections.singletonMap("queryUri", getQueryUri(field))))
                       : Collections.singletonList(localized(field, "common-validators", "uniqueValue.not-unique"));
            }
        }

        return Collections.emptyList();
    }

    protected String getQueryUri(InstanceField field) {
        String query = field.fieldDefinition.name.toLowerCase()
            .replaceAll(" ", "_") + ".raw:\"" + field.getValue() + "\"";

        return "/recordm/index.html#/definitions/" + field.instance.definition.id + "/q=" + query;
    }
}
