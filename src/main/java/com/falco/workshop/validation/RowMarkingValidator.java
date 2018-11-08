package com.falco.workshop.validation;

import java.util.List;

import static com.google.common.collect.ImmutableSet.of;

public class RowMarkingValidator implements RowValidator {
    private final Validator overlappingValidator;
    private final ValidationMessage validationMessage;

    public RowMarkingValidator(Validator overlappingValidator, ValidationMessage message) {
        this.overlappingValidator = overlappingValidator;
        this.validationMessage = message;
    }


    @Override
    public void validate(List<Row> rows) {
        new MessageValidator(of(validationMessage)).addVM(overlappingValidator.findConflicts(rows));
    }


}
