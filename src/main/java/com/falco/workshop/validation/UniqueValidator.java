package com.falco.workshop.validation;

import static com.falco.workshop.validation.GroupingValidator.grouping;
import static com.falco.workshop.validation.RowCountValidator.rowCount;

public class UniqueValidator {
    public static <T> Validator<T> unique(String property) {
        return grouping(property, rowCount(1));
    }

    public static <T> Validator<T> unique() {
        return grouping(rowCount(1));
    }
}
