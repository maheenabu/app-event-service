package com.example.msc.app.validation;

import java.util.Collections;
import java.util.List;

public final class FlattenResult<T> {
    private final List<T> items;
    private final List<ValidationError> errors;

    private FlattenResult(List<T> items, List<ValidationError> errors) {
        this.items = items == null ? Collections.emptyList() : Collections.unmodifiableList(items);
        this.errors = errors == null ? Collections.emptyList() : Collections.unmodifiableList(errors);
    }

    public static <T> FlattenResult<T> of(List<T> items, List<ValidationError> errors) {
        return new FlattenResult<>(items, errors);
    }

    public List<T> getItems()  { return items; }
    public List<ValidationError> getErrors() { return errors; }
    public boolean isOk() { return errors.isEmpty(); }
}
