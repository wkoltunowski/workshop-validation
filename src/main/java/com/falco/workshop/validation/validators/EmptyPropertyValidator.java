package com.falco.workshop.validation.validators;

import com.falco.workshop.validation.Validator;

import java.util.Objects;

import static com.falco.workshop.validation.validators.MapValidator.mapping;
import static java.util.stream.Collectors.toList;

public class EmptyPropertyValidator {

    public static <T> Validator<T> emptyProperty(String property) {
        return mapping(property, rows -> rows.stream().filter(Objects::isNull).collect(toList()));
    }
}
