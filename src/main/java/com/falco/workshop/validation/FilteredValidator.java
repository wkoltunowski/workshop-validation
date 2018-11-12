package com.falco.workshop.validation;

import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

import static com.falco.workshop.validation.GroupingValidator.read;
import static java.util.stream.Collectors.toList;

public class FilteredValidator<T> implements Validator<T> {
    private final Predicate<T> predicate;
    private final Validator<T> validator;

    public FilteredValidator(Predicate<T> predicate, Validator<T> validator) {
        this.predicate = predicate;
        this.validator = validator;
    }

    @Override
    public List<T> findConflicts(List<T> rows) {
        return validator.findConflicts(rows.stream().filter(predicate).collect(toList()));
    }

    public static <T> Validator<T> filterEmpty(String property, Validator<T> validator) {
        return new FilteredValidator<>(t -> read(property, t) != null, validator);
    }

    public static <T> Validator<T> filterEmpty(Validator<T> validator) {
        return new FilteredValidator<>(Objects::nonNull, validator);
    }


}
