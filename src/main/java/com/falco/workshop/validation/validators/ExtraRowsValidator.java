package com.falco.workshop.validation.validators;


import com.falco.workshop.validation.Validator;
import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.List;

public class ExtraRowsValidator<T> implements Validator<T> {
    private final List<T> extraRows;
    private final Validator<T> validator;

    private ExtraRowsValidator(List<T> extraRows, Validator<T> validator) {
        this.extraRows = extraRows;
        this.validator = validator;
    }

    @Override
    public List<T> findConflicts(List<T> rows) {
        ImmutableList<T> allRows = addExtraRows(rows);
        List<T> conflicts = validator.findConflicts(allRows);
        return removeExtraRows(conflicts);
    }

    private List<T> removeExtraRows(List<T> conflicts) {
        List<T> result = new ArrayList<>(conflicts);
        result.removeAll(extraRows);
        return result;
    }

    private ImmutableList<T> addExtraRows(List<T> rows) {
        return new ImmutableList.Builder<T>()
                .addAll(rows)
                .addAll(extraRows)
                .build();
    }

    public static <T> Validator<T> extraRows(List<T> extraRows, Validator<T> validator) {
        return new ExtraRowsValidator<>(extraRows, validator);
    }
}
