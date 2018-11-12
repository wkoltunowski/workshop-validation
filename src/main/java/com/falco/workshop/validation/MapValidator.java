package com.falco.workshop.validation;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import static com.falco.workshop.validation.GroupingValidator.rowReader;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

public class MapValidator<S, T> implements Validator<S> {

    private final Validator<T> validator;
    private final Function<S, T> map;

    public MapValidator(Function<S, T> map, Validator<T> validator) {
        this.map = map;
        this.validator = validator;
    }

    @Override
    public List<S> findConflicts(List<S> rows) {
        Map<Optional<T>, List<S>> mappings = rows.stream().collect(groupingBy(map.andThen(Optional::ofNullable)));
        List<T> mappedRows = rows.stream().map(map).collect(toList());
        List<T> conflicts = validator.findConflicts(mappedRows);
        return conflicts.stream().flatMap(c -> mappings.get(Optional.ofNullable(c)).stream()).collect(toList());


    }

    public static <S, T> Validator<S> mapping(Function<S, T> map, Validator<T> validator) {
        return new MapValidator<>(map, validator);
    }
    public static <S, T> Validator<S> mapping(String property, Validator<T> validator) {
        return new MapValidator<>(rowReader(property), validator);
    }
}
