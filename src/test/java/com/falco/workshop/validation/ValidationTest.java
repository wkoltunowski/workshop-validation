package com.falco.workshop.validation;

import org.junit.Test;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import static com.falco.workshop.validation.ExtraRowsValidator.extraRows;
import static com.falco.workshop.validation.FilteredValidator.filterEmpty;
import static com.falco.workshop.validation.GroupingValidator.grouping;
import static com.falco.workshop.validation.MapValidator.mapping;
import static com.falco.workshop.validation.OverlappingValidator.overlapping;
import static com.falco.workshop.validation.RowCountValidator.rowCount;
import static com.falco.workshop.validation.RowMarkingValidator.rowValidator;
import static com.falco.workshop.validation.UniqueValidator.unique;
import static com.falco.workshop.validation.ValidationMessage.validationError;
import static com.google.common.collect.ImmutableList.of;
import static java.util.Arrays.stream;
import static org.assertj.core.api.Assertions.assertThat;

public class ValidationTest {

    private Table table;

    @Test
    public void shouldDetectOverlappingRows() {
        givenValidator(
                "msg.overlapping.dates",
                overlapping("from", "to"));
        validateRows(
                row(from("2018-01-01 12:15"), to("2018-01-01 12:30")),
                row(from("2018-01-01 12:30"), to("2018-01-01 12:45")));

        assertThat(rowValidationResults(0)).containsOnly(validationError("msg.overlapping.dates"));
        assertThat(rowValidationResults(1)).containsOnly(validationError("msg.overlapping.dates"));
    }

    @Test
    public void shouldDetectUniqueCode() {
        givenValidator(
                "msg.unique",
                unique("code"));
        validateRows(
                row(code("A")),
                row(code("B")),
                row(code("A")));

        assertThat(rowValidationResults(0)).containsOnly(validationError("msg.unique"));
        assertThat(rowValidationResults(1)).isEmpty();
        assertThat(rowValidationResults(2)).containsOnly(validationError("msg.unique"));
    }

    @Test
    public void shouldNotDetectOverlappingRows() {
        givenValidator(
                "msg.overlapping.dates",
                overlapping("from", "to"));
        validateRows(
                row(from("2018-01-01 12:15"), to("2018-01-01 12:30")),
                row(from("2018-01-01 12:31"), to("2018-01-01 12:45")));

        assertThat(rowValidationResults(0)).isEmpty();
        assertThat(rowValidationResults(1)).isEmpty();
    }

    @Test
    public void shouldDetectUniqueCodeForOverlappingRangesOnly() {
        givenValidator(
                "msg.unique.within.overlapping",
                grouping("code", overlapping("from", "to"))
        );
        validateRows(
                row(from("2018-01-01 12:15"), to("2018-01-01 12:30"), code("A")),
                row(from("2018-01-01 12:20"), to("2018-01-01 12:45"), code("A")),
                row(from("2018-01-01 12:50"), to("2018-01-01 13:00"), code("A"))
        );

        assertThat(rowValidationResults(0)).containsOnly(validationError("msg.unique.within.overlapping"));
        assertThat(rowValidationResults(1)).containsOnly(validationError("msg.unique.within.overlapping"));
        assertThat(rowValidationResults(2)).isEmpty();
    }

    @Test
    public void shouldIgnoreNulls() {
        givenValidator(
                "msg.unique",
                mapping("code", filterEmpty(grouping(rowCount(1)))));
        validateRows(
                row(code("A")),
                row(code(null)));

        assertThat(rowValidationResults(0)).isEmpty();
        assertThat(rowValidationResults(1)).isEmpty();
    }

    @Test
    public void shouldDetectUniqueNulls() {
        givenValidator(
                "msg.unique.null",
                mapping("code", unique()));
        validateRows(
                row(code(null)),
                row(code(null)));

        assertThat(rowValidationResults(0)).containsOnly(validationError("msg.unique.null"));
        assertThat(rowValidationResults(1)).containsOnly(validationError("msg.unique.null"));
    }

    @Test
    public void shouldDetectUniqueAmongNotVisibleRows() {
        givenValidator(
                "msg.extra.rows",
                extraRows(of(row(code("A"))), unique("code")));
        validateRows(row(code("B")), row(code("A")));

        assertThat(rowValidationResults(0)).isEmpty();
        assertThat(rowValidationResults(1)).containsOnly(validationError("msg.extra.rows"));
    }

    private void validateRows(Row... rows) {
        table.addRows(rows);
        table.validateTable();
    }

    private void givenValidator(String msg, Validator<Row> validator) {
        table = new Table(of(rowValidator(
                validator,
                validationError(msg))));
    }

    private Set<ValidationMessage> rowValidationResults(int i) {
        return table.rowAt(i).validationResults();
    }

    private Consumer<Map<String, Object>> from(String from) {
        return attributes -> attributes.put("from", date(from));
    }

    private Consumer<Map<String, Object>> to(String to) {
        return attributes -> attributes.put("to", date(to));
    }

    private Consumer<Map<String, Object>> code(String value) {
        return attributes -> attributes.put("code", value);
    }

    private LocalDateTime date(String from) {
        return LocalDateTime.parse(from.replaceAll(" ", "T"));
    }

    @SafeVarargs
    private final Row row(Consumer<Map<String, Object>>... consumers) {
        Map<String, Object> attributesMap = new HashMap<>();
        stream(consumers).forEach(c -> c.accept(attributesMap));
        return new Row(attributesMap);
    }
}
