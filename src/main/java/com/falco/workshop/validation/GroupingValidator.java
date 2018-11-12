package com.falco.workshop.validation;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

public class GroupingValidator<T> implements Validator<T> {
    private final Validator<T> validator;
    private final Function<T, ?> groupFct;

    private GroupingValidator(Function<T, ?> groupFct, Validator<T> validator) {
        this.groupFct = groupFct.andThen(Optional::ofNullable);
        this.validator = validator;
    }

    @Override
    public List<T> findConflicts(List<T> rows) {
        Collection<List<T>> groups = rows.stream().collect(groupingBy(groupFct)).values();
        return groups.stream()
                .map(validator::findConflicts)
                .flatMap(Collection::stream)
                .collect(toList());
    }

    public static <T> Validator<T> grouping(String propertyName, Validator<T> validator) {
        return grouping(validator, rowReader(propertyName));
    }
    public static <T> Validator<T> grouping(Validator<T> validator) {
        return grouping(validator, Function.identity());
    }

    private static <T> Validator<T> grouping(Validator<T> validator, Function<T, ?> grpFct) {
        return new GroupingValidator<>(grpFct, validator);
    }

    public static <T, S> Function<T, S> rowReader(String propertyName) {
        return o -> ((Row) o).readAs(propertyName);
    }
    public static <T> Object read(String property, T t) {
        return GroupingValidator.rowReader(property).apply(t);
    }
}
