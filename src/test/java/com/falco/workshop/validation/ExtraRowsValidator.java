package com.falco.workshop.validation;

import org.assertj.core.util.Lists;

import java.util.ArrayList;
import java.util.List;

public class ExtraRowsValidator implements Validator {
    private final List<Row> extraRows;
    private final Validator validator;

    public ExtraRowsValidator(List<Row> extraRows, Validator validator) {
        this.extraRows = extraRows;
        this.validator = validator;
    }

    @Override
    public List<Row> findConflicts(List<Row> rows) {
        ArrayList<Row> allRows = Lists.newArrayList(rows);
        allRows.addAll(extraRows);

        List<Row> conflicts = validator.findConflicts(allRows);
        conflicts.removeAll(extraRows);
        return conflicts;
    }
}
