package com.falco.workshop.validation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static java.util.Arrays.asList;

public class Table {
    private final List<Row> rows = new ArrayList<>();
    private final List<RowValidator> validators = new ArrayList<>();

    public Table(Collection<RowValidator> validators) {
        this.validators.addAll(validators);
    }

    public void validateTable() {
        for (Row row : rows) {
            row.cleanValidation();
        }
        for (RowValidator validator : validators) {
            validator.validate(rows);
        }
    }

    public Row rowAt(int i) {
        return rows.get(i);
    }

    public void addRows(Row... row) {
        this.rows.addAll(asList(row));
    }
}
