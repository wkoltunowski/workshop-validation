package com.falco.workshop.validation.validators;

import com.falco.workshop.validation.Row;
import com.falco.workshop.validation.Validator;

import static com.falco.workshop.validation.validators.OverlappingValidator.closedRange;
import static java.util.stream.Collectors.toList;

public class IntervalValidator {
    public static Validator<Row> invalidInterval(String from, String to) {
        return rows -> rows.stream().filter(r -> closedRange(from, to).apply(r).isEmpty()).collect(toList());
    }
}
