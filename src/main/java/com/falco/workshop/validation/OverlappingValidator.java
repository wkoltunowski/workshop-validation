package com.falco.workshop.validation;

import com.google.common.collect.Lists;
import com.google.common.collect.Range;

import java.util.List;

public class OverlappingValidator implements Validator {

    private final String fromProperty;
    private final String toProperty;

    public OverlappingValidator(String fromProperty, String toProperty) {
        this.fromProperty = fromProperty;
        this.toProperty = toProperty;
    }

    @Override
    public List<Row> findConflicts(List<Row> rows) {
        List<Row> overlapping = Lists.newArrayList();
        for (Row another : rows) {
            for (Row row : rows) {
                if (overlaps(rowRange(row), rowRange(another)) && !row.equals(another)) {
                    overlapping.add(another);
                }
            }
        }
        return overlapping;
    }

    private boolean overlaps(Range r1, Range r2) {
        return r1.isConnected(r2) && !r1.intersection(r2).isEmpty();
    }

    private Range rowRange(Row row) {
        return Range.closed(
                row.readAs(fromProperty),
                row.readAs(toProperty)
        );
    }
}
