package com.falco.workshop.validation;

import org.junit.Test;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import static com.falco.workshop.validation.ValidationMessage.validationError;
import static com.google.common.collect.ImmutableList.of;
import static java.util.Arrays.stream;
import static org.assertj.core.api.Assertions.assertThat;

public class OrgUnitValidatorTest {

    private Table table;

    private void validateRows(Row... rows) {
        table = new Table(of(new OrgUnitValidator()));
        table.addRows(rows);
        table.validateTable();
    }

    @Test
    public void shouldDetectDuplicateCodes() {
        validateRows(
                row(from("2018-01-01"), to("2018-01-31"), code("POR_1")),
                row(from("2018-01-10"), to("2018-02-01"), code("POR_1")));

        assertThat(rowValidationResults(0)).containsOnly(validationError("msg.overlapping.codes"));
        assertThat(rowValidationResults(1)).containsOnly(validationError("msg.overlapping.codes"));
    }

    @Test
    public void shouldDetectDuplicateCodesInAllRows() {
        validateRows(
                row(from("2018-01-01"), to("2018-01-30"), code("POR_1")),
                row(from("2018-01-10"), to("2018-01-15"), code("POR_1")),
                row(from("2018-01-20"), to("2018-02-01"), code("POR_1")));

        assertThat(rowValidationResults(0)).containsOnly(validationError("msg.overlapping.codes"));
        assertThat(rowValidationResults(1)).containsOnly(validationError("msg.overlapping.codes"));
    }

    @Test
    public void shouldNotDetectWhenDifferentCodes() {
        validateRows(
                row(from("2018-01-01"), to("2018-01-31"), code("POR_1")),
                row(from("2018-01-10"), to("2018-02-01"), code("POR_2")));

        assertThat(rowValidationResults(0)).isEmpty();
        assertThat(rowValidationResults(1)).isEmpty();
    }

    @Test
    public void shouldNotDetectDuplicateCodesWhenNoOverlap() {
        validateRows(
                row(from("2018-01-01"), to("2018-01-10"), code("POR_1")),
                row(from("2018-01-11"), to("2018-01-31"), code("POR_1")));

        assertThat(rowValidationResults(0)).isEmpty();
        assertThat(rowValidationResults(1)).isEmpty();
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

    private LocalDate date(String day) {
        return day != null ? LocalDate.parse(day) : null;
    }

    @SafeVarargs
    private final Row row(Consumer<Map<String, Object>>... consumers) {
        Map<String, Object> attributesMap = new HashMap<>();
        stream(consumers).forEach(c -> c.accept(attributesMap));
        return new Row(attributesMap);
    }
}
