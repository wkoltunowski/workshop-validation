package com.falco.workshop.validation;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Row {

    private final Map<String, Object> attributes = new HashMap<>();
    private ImmutableSet<ValidationMessage> validationResults = ImmutableSet.of();

    public Row(Map<String, Object> attributes) {
        this.attributes.putAll(attributes);
    }

    public Set<ValidationMessage> validationResults() {
        return this.validationResults;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SIMPLE_STYLE);
    }

    public void addValidationMessages(Collection<ValidationMessage> validationMessages) {
        this.validationResults =
                new Builder<ValidationMessage>()
                        .addAll(validationResults)
                        .addAll(validationMessages)
                        .build();
    }

    public <T> T readAs(String property) {
        return (T) attributes.get(property);
    }

    public void cleanValidation() {
        this.validationResults = ImmutableSet.of();
    }
}
