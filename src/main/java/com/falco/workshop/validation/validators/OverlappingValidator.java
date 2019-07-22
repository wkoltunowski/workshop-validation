package com.falco.workshop.validation.validators;

import com.falco.workshop.validation.Validator;
import com.google.common.collect.Lists;
import com.google.common.collect.Range;

import java.util.List;
import java.util.function.Function;

import static com.falco.workshop.validation.validators.GroupingValidator.read;
import static com.falco.workshop.validation.validators.MapValidator.mapping;

public class OverlappingValidator<T extends Range<Comparable<?>>> implements Validator<T> {

    @Override
    public List<T> findConflicts(List<T> ranges) {
        List<T> overlappings = Lists.newArrayList();
        for (T another : ranges) {
            for (T row : ranges) {
                if (!row.equals(another) && overlap(row, another)) {
                    overlappings.add(another);
                }
            }
        }
        return overlappings;
    }

    private boolean overlap(T r1, T r2) {
        return r1.isConnected(r2) && !r1.intersection(r2).isEmpty();
    }

    public static <T> Validator<T> overlapping(String from, String to) {
        return FilteredValidator.filtered(
                r -> !closedRange(from, to).apply(r).isEmpty(),
                mapping(closedRange(from, to), new OverlappingValidator<>()));
    }

    static <T> Function<T, Range> closedRange(String from, String to) {
        return t -> {
            Comparable lower = readComparable(t, from);
            Comparable upper = readComparable(t, to);
            if (lower == null && upper == null) return Range.all();
            if (lower != null && upper != null) {
                if (lower.compareTo(upper) > 0) return Range.closedOpen(lower, lower);
                return Range.closed(lower, upper);
            }
            if (upper == null) return Range.atLeast(lower);
            return Range.atMost(upper);
        };
    }

    private static <T> Comparable readComparable(T t, String from) {
        return (Comparable) read(from, t);
    }
}
