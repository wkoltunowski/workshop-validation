package com.falco.workshop.validation.validators;

import com.falco.workshop.validation.Validator;

import static com.falco.workshop.validation.validators.GroupingValidator.groupingBy;
import static com.falco.workshop.validation.validators.RowCountValidator.rowCount;

public class UniqueValidator {
    public static <T> Validator<T> unique(String property) {
        return GroupingValidator.groupingBy(property, rowCount(1));
    }

    public static <T> Validator<T> unique() {
        return groupingBy(rowCount(1));
    }
}
