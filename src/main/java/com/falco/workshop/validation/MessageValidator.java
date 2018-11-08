package com.falco.workshop.validation;

import com.google.common.collect.ImmutableSet;

import java.util.Collection;
import java.util.List;

public class MessageValidator {

    private final Collection<ValidationMessage> validationMessages;

    public MessageValidator(ImmutableSet<ValidationMessage> validationMessages) {
        this.validationMessages = validationMessages;
    }

    public void addVM(List<Row> rows) {
        for (Row row : rows) {
            row.addValidationMessages(validationMessages);
        }
    }
}
