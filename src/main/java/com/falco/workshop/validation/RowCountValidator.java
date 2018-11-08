package com.falco.workshop.validation;

import java.util.Collections;
import java.util.List;

public class RowCountValidator implements Validator {
    private final int count;

    public RowCountValidator(int count) {
        this.count = count;
    }

    @Override
    public List<Row> findConflicts(List<Row> rows) {
        return rows.size() > count ?rows: Collections.emptyList();
    }
}
