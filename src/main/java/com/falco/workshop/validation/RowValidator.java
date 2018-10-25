package com.falco.workshop.validation;

import java.util.List;
import java.util.Set;

public interface RowValidator {
    Set<ValidationMessage> validate(Row row, List<Row> rows);
}
