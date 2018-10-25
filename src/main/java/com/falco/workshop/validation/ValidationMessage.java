package com.falco.workshop.validation;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.List;

public class ValidationMessage {
    private final String msg;
    private final List<String> properties;

    public ValidationMessage(String msg, List<String> properties) {
        this.msg = msg;
        this.properties = properties;
    }

    @Override
    public boolean equals(Object o) {
        return EqualsBuilder.reflectionEquals(this, o);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SIMPLE_STYLE);
    }

    public static ValidationMessage validationError(String msg, List<String> properties) {
        return new ValidationMessage(msg, properties);
    }
}
