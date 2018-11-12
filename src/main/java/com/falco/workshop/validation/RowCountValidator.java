package com.falco.workshop.validation;

import com.google.common.collect.ImmutableList;

import java.util.List;

public class RowCountValidator<T> implements Validator<T> {
    private final int count;

    private RowCountValidator(int count) {
        this.count = count;
    }

    @Override
    public List<T> findConflicts(List<T> rows) {
        return rows.size() > count ? rows : ImmutableList.of();
    }

    public static <T> Validator<T> rowCount(int count) {
        return new RowCountValidator<>(count);
    }
}
