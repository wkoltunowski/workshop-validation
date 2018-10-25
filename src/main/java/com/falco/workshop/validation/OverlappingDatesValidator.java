package com.falco.workshop.validation;

import com.google.common.collect.Range;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static com.falco.workshop.validation.ValidationMessage.validationError;
import static com.google.common.collect.ImmutableSet.of;
import static java.util.Arrays.asList;

public class OverlappingDatesValidator implements RowValidator {
    @Override
    public Set<ValidationMessage> validate(Row row, List<Row> rows) {
        for (Row another : rows) {
            if (overlaps(rowRange(row), rowRange(another))) {
                return of(validationError("msg.validation.overlapping.dates", asList("from", "to")));
            }
        }
        return of();
    }

    private boolean overlaps(Range<LocalDateTime> r1, Range<LocalDateTime> r2) {
        return !r1.intersection(r2).isEmpty();
    }

    private Range<LocalDateTime> rowRange(Row row) {
        return Range.closedOpen(
                row.readAs("from"),
                row.readAs("to")
        );
    }
}
