package com.falco.workshop.validation;

import java.time.LocalDate;
import java.util.List;

import static com.falco.workshop.validation.ValidationMessage.validationError;

public class OrgUnitValidator implements RowValidator {

    private final ValidationMessage OVERLAPPING = validationError("msg.overlapping.codes");

    @Override
    public void validate(List<Row> rows) {
    }

    private String code(Row row) {
        return row.readAs("code");
    }

    private LocalDate to(Row row) {
        return row.readAs("to");
    }

    private LocalDate from(Row row) {
        return row.readAs("from");
    }
}
