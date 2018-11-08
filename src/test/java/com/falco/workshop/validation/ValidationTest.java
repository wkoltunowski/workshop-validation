package com.falco.workshop.validation;

import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import static com.falco.workshop.validation.ValidationMessage.validationError;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

public class ValidationTest {

    private Table table;

    private List<RowValidator> validator() {
        return asList(new RowMarkingValidator(
                new OverlappingValidator("from", "to"),
                validationError("msg.validation.overlapping.dates")));
    }

    @Before
    public void setUp() throws Exception {
        table = new Table(validator());
    }

    @Test
    public void shouldDetectOverlappingRows() {
        table.addRows(
                row(from("2018-01-01 12:15"), to("2018-01-01 12:30")),
                row(from("2018-01-01 12:30"), to("2018-01-01 12:45")));
        table.validateTable();
        assertThat(rowValidationResults(0)).containsOnly(validationError("msg.validation.overlapping.dates"));
        assertThat(rowValidationResults(1)).containsOnly(validationError("msg.validation.overlapping.dates"));
    }

    @Test
    public void shouldNotDetectOverlappingRows() {
        Table table = new Table(validator());
        table.addRows(
                row(from("2018-01-01 12:15"), to("2018-01-01 12:30")),
                row(from("2018-01-01 12:31"), to("2018-01-01 12:45")));
        table.validateTable();
        assertThat(table.rowAt(0).validationResults()).isEmpty();
        assertThat(table.rowAt(1).validationResults()).isEmpty();
    }

    @Test
    public void shouldDetectOverlappingRowsWithinGroup() {
        Table table = new Table(asList(new RowMarkingValidator(
                new GroupingValidator("group", new OverlappingValidator("from", "to")),
                validationError("msg.validation.overlapping.dates.in.group"))));
        table.addRows(
                row(from("2018-01-01 12:15"), to("2018-01-01 12:30"), group("A")),
                row(from("2018-01-01 12:15"), to("2018-01-01 12:30"), group("B")),
                row(from("2018-01-01 12:30"), to("2018-01-01 12:45"), group("A")));
        table.validateTable();
        assertThat(table.rowAt(0).validationResults()).containsOnly(validationError("msg.validation.overlapping.dates.in.group"));
        assertThat(table.rowAt(1).validationResults()).isEmpty();
        assertThat(table.rowAt(2).validationResults()).containsOnly(validationError("msg.validation.overlapping.dates.in.group"));
    }

    @Test
    public void shouldDetectUnique() {
        Table table = new Table(asList(new RowMarkingValidator(
                new GroupingValidator("group", new RowCountValidator(1)),
                validationError("msg.validation.overlapping.dates.in.group"))));
        table.addRows(
                row(from("2018-01-01 12:15"), to("2018-01-01 12:30"), group("A")),
                row(from("2018-01-01 12:15"), to("2018-01-01 12:30"), group("B")),
                row(from("2018-01-01 12:30"), to("2018-01-01 12:45"), group("A")));
        table.validateTable();
        assertThat(table.rowAt(0).validationResults()).containsOnly(validationError("msg.validation.overlapping.dates.in.group"));
        assertThat(table.rowAt(1).validationResults()).isEmpty();
        assertThat(table.rowAt(2).validationResults()).containsOnly(validationError("msg.validation.overlapping.dates.in.group"));
    }

    @Test
    public void shouldDetectUniqueAmongNotVisibleRows() {
        Table table = new Table(asList(new RowMarkingValidator(
                new ExtraRowsValidator(asList(row(group("A"))), new GroupingValidator("group", new RowCountValidator(1))),
                validationError("msg.validation.overlapping.dates.against.extra.rows"))));

        table.addRows(
                row(group("B")),
                row(group("A")));

        table.validateTable();
        assertThat(table.rowAt(0).validationResults()).isEmpty();
        assertThat(table.rowAt(1).validationResults()).containsOnly(validationError("msg.validation.overlapping.dates.against.extra.rows"));
    }

    private Set<ValidationMessage> rowValidationResults(int i) {
        return table.rowAt(i).validationResults();
    }

    private Consumer<Map<String, Object>> group(String value) {
        return attributes -> attributes.put("group", value);
    }


    private Consumer<Map<String, Object>> to(String to) {
        return attributes -> attributes.put("to", date(to));
    }

    private Consumer<Map<String, Object>> from(String from) {
        return attributes -> attributes.put("from", date(from));
    }

    private LocalDateTime date(String from) {
        return LocalDateTime.parse(from.replaceAll(" ", "T"));
    }

    @SafeVarargs
    private final Row row(Consumer<Map<String, Object>>... attributes) {
        Map<String, Object> attributesMap = new HashMap<>();
        asList(attributes).forEach(c -> c.accept(attributesMap));
        return new Row(attributesMap);
    }
}
