package com.falco.workshop.validation;

import java.util.List;

import static com.google.common.collect.ImmutableSet.of;

public class RowMarkingValidator implements RowValidator {
    private final Validator<Row> validator;
    private final ValidationMessage validationMessage;

    private RowMarkingValidator(Validator<Row> validator, ValidationMessage message) {
        this.validator = validator;
        this.validationMessage = message;
    }


    @Override
    public void validate(List<Row> rows) {
        addValidationMessages(validator.findConflicts(rows));
    }

    private void addValidationMessages(List<Row> rows) {
        for (Row row : rows) {
            row.addValidationMessages(of(validationMessage));
        }
    }


    public static RowMarkingValidator rowValidator(Validator<Row> validator, ValidationMessage message) {
        return new RowMarkingValidator(validator, message);
    }
}
