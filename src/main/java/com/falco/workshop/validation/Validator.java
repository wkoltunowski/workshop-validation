package com.falco.workshop.validation;

import java.util.List;

public interface Validator {
    List<Row> findConflicts(List<Row> rows);
}
