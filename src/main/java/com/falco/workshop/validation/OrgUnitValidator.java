package com.falco.workshop.validation;

import java.time.LocalDate;
import java.util.List;

import static com.falco.workshop.validation.ValidationMessage.validationError;
import static com.google.common.collect.ImmutableList.of;

public class OrgUnitValidator implements RowValidator {

    @Override
    public void validate(List<Row> rows) {
        for (Row row : rows) {
            if (row.readAs("code") == null) {
                row.addValidationMessages(of(validationError("msg.empty.code")));
            }
            if (row.readAs("company") == null) {
                row.addValidationMessages(of(validationError("msg.empty.company")));
            }
        }
        for (Row row : rows) {
            for (Row another : rows) {
                if (from(row) != null && to(row) != null && from(row).isAfter(to(row))) {
                    row.addValidationMessages(of(validationError("msg.invalid.interval")));
                } else if (row != another &&
                        row.readAs("code") != null && row.readAs("code").equals(another.readAs("code")) &&
                        row.readAs("company") != null && row.readAs("company").equals(another.readAs("company")) &&
                        overlap(row, another)) {
                    another.addValidationMessages(of(validationError("msg.overlapping.codes")));
                }
            }
        }
    }

    private boolean overlap(Row row, Row another) {
        Row first = from(row) == null || from(row).isBefore(from(another)) ? row : another;
        Row second = from(row) == null || from(row).isBefore(from(another)) ? another : row;
        return from(second) == null || to(first) == null || !from(second).isAfter(to(first));
    }

    private LocalDate to(Row row) {
        return row.readAs("to");
    }

    private LocalDate from(Row row) {
        return row.readAs("from");
    }
}
