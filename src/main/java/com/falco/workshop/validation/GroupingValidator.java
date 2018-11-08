package com.falco.workshop.validation;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.google.common.collect.Lists.newArrayList;

public class GroupingValidator implements Validator{
    private final String property;
    private final Validator validator;

    public GroupingValidator(String propertyName, Validator validator) {
        this.property = propertyName;
        this.validator = validator;
    }

    @Override
    public List<Row> findConflicts(List<Row> rows) {
        Map<Object, List<Row>> grouped = rows.stream().collect(Collectors.groupingBy(r -> r.readAs(property)));
        List<Row> conflicts = newArrayList();
        for (List<Row> rowList : grouped.values()) {
            conflicts.addAll(validator.findConflicts(rowList));
        }
        return conflicts;
    }
}
