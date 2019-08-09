package com.falco.workshop.validation.validators;

import com.falco.workshop.validation.Validator;
import com.google.common.collect.Range;
import com.google.common.collect.RangeSet;
import com.google.common.collect.TreeRangeSet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.falco.workshop.validation.validators.MapValidator.mapping;
import static com.falco.workshop.validation.validators.OverlappingValidator.closedRange;
import static java.util.stream.Collectors.toList;

public class GuavaOverlappingValidator<T extends Range<Comparable<?>>> implements Validator<T> {

    @Override
    public List<T> findConflicts(List<T> ranges) {
        RangeSet<Comparable<?>> rangeSet = TreeRangeSet.create();
        ranges.forEach(rangeSet::add);

        Map<T, List<T>> rangesMap = new HashMap<>();
        for (T row : ranges) {
            T canonicalRange = (T) rangeSet.rangeContaining(row.lowerEndpoint());
            rangesMap.putIfAbsent(canonicalRange, new ArrayList<>());
            rangesMap.get(canonicalRange).add(row);
        }

        //mark conflicting rows
        return rangesMap.values().stream().filter(l -> l.size() > 1).flatMap(Collection::stream).collect(toList());
    }

    public static <T> Validator<T> guavaOverlapping(String from, String to) {
        return FilteredValidator.filtered(
                r -> !closedRange(from, to).apply(r).isEmpty(),
                mapping(closedRange(from, to), new GuavaOverlappingValidator<>()));
    }


}
