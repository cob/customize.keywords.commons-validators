package com.cultofbits.customizations.validators;

import com.cultofbits.customizations.validators.impl.*;
import com.cultofbits.recordm.core.model.FieldDefinition;
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

import static com.cultofbits.customizations.validators.impl.Action.ADD;
import static com.cultofbits.customizations.validators.impl.Action.UPDATE;

/**
 * Email validator that uses the same email validator when registering users in UserM.
 * This is important when using scripts in IntegrationM that create users in UserM
 */
public class CommonsValidatorService implements OnCreateValidator, OnUpdateValidator {

    public static final String KEYWORD = "$commons.validate";

    public List<CommonValidator> validators = null;
    public InstanceRepository instanceRepository;

    @Override
    public void setInstanceRepository(InstanceRepository instanceRepository) {
        this.instanceRepository = instanceRepository;
    }

    private List<CommonValidator> getCommonValidators() {
        if (validators == null) {
            validators = new ArrayList<>();
            validators.add(new EmailValidator());
            validators.add(new RegexValidator());
            validators.add(new NoUpdateValidator());
            validators.add(new UniqueValidator(instanceRepository));
        }

        return validators;
    }

    @Override
    public Collection<ValidationError> onCreate(Instance instance) {
        return validateInstanceFields(instance.getRootFields(), null, ADD);
    }

    @Override
    public Collection<ValidationError> onUpdate(Instance persistedInstance, Instance updatedInstance) {
        return validateInstanceFields(updatedInstance.getRootFields(), persistedInstance, UPDATE);
    }

    public Collection<ValidationError> validateInstanceFields(List<InstanceField> instanceFields,
                                                              Instance persistedInstance,
                                                              Action action) {
        List<ValidationError> errors = new ArrayList<>();

        for (InstanceField instanceField : instanceFields) {
            if ((!instanceField.isVisible() || instanceField.getValue() == null)
                && instanceField.children.isEmpty()) {
                continue;
            }

            FieldDefinition fieldDefinition = instanceField.fieldDefinition;

            if (fieldDefinition.containsExtension(KEYWORD)) {
                if (instanceField.getValue() == null) {
                    return Collections.emptyList();
                }

                List<Object> requestedValidators = new ArrayList<>(fieldDefinition.getConfiguration().args(KEYWORD).toMap().values());
                requestedValidators = (ArrayList<Object>) requestedValidators.get(0);

                requestedValidators.forEach(fieldVal -> getCommonValidators().forEach(val -> {
                    String valType = String.valueOf(fieldVal).trim();

                    if (val.supports(valType)) {
                        List<ValidationError> validationErrors = new ArrayList<>();

                        if (action == ADD) {
                            validationErrors.addAll(val.validateOnCreate(instanceField, valType));

                        } else {
                            validationErrors.addAll(val.validateOnUpdate(instanceField,
                                                                         instanceField.id != null ? persistedInstance.getFieldIfExists(instanceField.id) : null,
                                                                         valType));
                        }


                        if (!validationErrors.isEmpty()) {
                            errors.addAll(validationErrors);
                        }
                    }
                }));
            }

            if (!instanceField.children.isEmpty()) {
                errors.addAll(validateInstanceFields(instanceField.children, persistedInstance, action));
            }
        }

        return errors;
    }
}
