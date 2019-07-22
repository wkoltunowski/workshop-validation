package com.falco.workshop.validation;

import org.junit.Test;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import static com.falco.workshop.validation.RowMarkingValidator.composite;
import static com.falco.workshop.validation.RowMarkingValidator.rowValidator;
import static com.falco.workshop.validation.ValidationMessage.validationError;
import static com.falco.workshop.validation.validators.EmptyPropertyValidator.emptyProperty;
import static com.falco.workshop.validation.validators.GroupingValidator.groupingBy;
import static com.falco.workshop.validation.validators.IntervalValidator.invalidInterval;
import static com.falco.workshop.validation.validators.OverlappingValidator.overlapping;
import static com.google.common.collect.ImmutableList.of;
import static java.util.Arrays.stream;
import static org.assertj.core.api.Assertions.assertThat;

public class OrgUnitValidatorTest {

    private Table table;

    private void givenValidator() {
        RowValidator v = composite(
                rowValidator("msg.empty.code", emptyProperty("code")),
                rowValidator("msg.empty.company", emptyProperty("company")),
                rowValidator("msg.invalid.interval", invalidInterval("from", "to")),
                rowValidator("msg.overlapping.codes", groupingBy(of("code", "company"), overlapping("from", "to")))
        );
//        table = new Table(of(new OrgUnitValidator()));
        table = new Table(of(v));
    }


    @Test
    public void shouldDetectEmptyCode() {
        givenValidator();
        validateRows(row(from("2018-01-01"), to("2018-01-31"), code(null), company("X")));
        assertThat(rowValidationResults(0)).containsOnly(validationError("msg.empty.code"));
    }

    @Test
    public void shouldDetectEmptyCompany() {
        givenValidator();
        validateRows(row(from("2018-01-01"), to("2018-01-31"), code("POR_1"), company(null)));
        assertThat(rowValidationResults(0)).containsOnly(validationError("msg.empty.company"));
    }

    @Test
    public void shouldDetectFromAfterTo() {
        givenValidator();
        validateRows(row(from("2018-02-01"), to("2018-01-01"), code("POR_1"), company("X")));
        assertThat(rowValidationResults(0)).containsOnly(validationError("msg.invalid.interval"));
    }


    @Test
    public void shouldDetectDuplicateCodes() {
        givenValidator();
        validateRows(
                row(from("2018-01-01"), to("2018-01-31"), code("POR_1"), company("X")),
                row(from("2018-01-10"), to("2018-02-01"), code("POR_1"), company("X")));

        assertThat(rowValidationResults(0)).containsOnly(validationError("msg.overlapping.codes"));
        assertThat(rowValidationResults(1)).containsOnly(validationError("msg.overlapping.codes"));
    }

    @Test
    public void shouldTreatNullAsInfinity() {
        givenValidator();
        validateRows(
                row(from("2018-01-01"), to(null), code("POR_1"), company("X")),
                row(from("2018-01-10"), to("2018-02-01"), code("POR_1"), company("X")));

        assertThat(rowValidationResults(0)).containsOnly(validationError("msg.overlapping.codes"));
        assertThat(rowValidationResults(1)).containsOnly(validationError("msg.overlapping.codes"));
    }

    @Test
    public void shouldNotDetectWhenDifferentCodes() {
        givenValidator();
        validateRows(
                row(from("2018-01-01"), to("2018-01-31"), code("POR_1"), company("X")),
                row(from("2018-01-10"), to("2018-02-01"), code("POR_2"), company("X")));

        assertThat(rowValidationResults(0)).isEmpty();
        assertThat(rowValidationResults(1)).isEmpty();
    }

    @Test
    public void shouldNotDetectDuplicateCodesWhenNoOverlap() {
        givenValidator();
        validateRows(
                row(from("2018-01-01"), to("2018-01-10"), code("POR_1"), company("X")),
                row(from("2018-01-11"), to("2018-01-31"), code("POR_1"), company("X")));

        assertThat(rowValidationResults(0)).isEmpty();
        assertThat(rowValidationResults(1)).isEmpty();
    }

    @Test
    public void shouldNotDetectDuplicateCodesWhenDifferentCompany() {
        givenValidator();
        validateRows(
                row(from("2018-01-01"), to("2018-01-10"), code("POR_1"), company("X")),
                row(from("2018-01-05"), to("2018-01-31"), code("POR_1"), company("Y")));

        assertThat(rowValidationResults(0)).isEmpty();
        assertThat(rowValidationResults(1)).isEmpty();
    }

    private Consumer<Map<String, Object>> company(String companyCode) {
        return m -> m.put("company", companyCode);
    }

    private void validateRows(Row... rows) {
        table.addRows(rows);
        table.validateTable();
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
