package com.falco.workshop.validation;

import com.google.common.collect.Lists;
import com.google.common.collect.Range;

import java.util.List;
import java.util.function.Function;

import static com.falco.workshop.validation.GroupingValidator.read;
import static com.falco.workshop.validation.GroupingValidator.rowReader;
import static com.falco.workshop.validation.MapValidator.mapping;

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
        return mapping(closedRange(from, to), new OverlappingValidator<>());
    }

    private static <T> Function<T, Range> closedRange(String from, String to) {
        return t -> Range.closed(readComparable(t, from), readComparable(t, to));
    }

    private static <T> Comparable<?> readComparable(T t, String from) {
        return (Comparable<?>) read(from,t);
    }
}
