package org.fix4j.sketch;

import java.util.Objects;

public final class SimpleFieldDefinition implements FieldDefinition {
    private final FieldType fieldType;

    public SimpleFieldDefinition(final FieldType fieldType) {
        this.fieldType = Objects.requireNonNull(fieldType);
    }

    @Override
    public boolean isRequired() {
        return false;
    }

    @Override
    public FieldDefinition setRequired() {
        return null;
    }

    @Override
    public Field createField() {
        return null;
    }
}
