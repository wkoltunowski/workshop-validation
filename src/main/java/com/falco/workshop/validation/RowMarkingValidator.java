package com.falco.workshop.validation;

import java.util.List;

import static com.falco.workshop.validation.ValidationMessage.validationError;
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


    public static RowMarkingValidator rowValidator(ValidationMessage message, Validator<Row> validator) {
        return new RowMarkingValidator(validator, message);
    }

    public static RowMarkingValidator rowValidator(String msg, Validator<Row> validator) {
        return new RowMarkingValidator(validator, validationError(msg));
    }

    public static RowValidator composite(RowValidator... validators) {
        return new RowValidator() {
            @Override
            public void validate(List<Row> rows) {
                for (RowValidator validator : validators) {
                    validator.validate(rows);
                }
            }
        };
    }
}
