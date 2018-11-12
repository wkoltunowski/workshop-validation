package com.falco.workshop.validation;

import java.util.List;

public interface Validator<T> {
    List<T> findConflicts(List<T> rows);
}
