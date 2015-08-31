package org.fix4j.sketch;

public interface FieldDefinition {
    boolean isRequired();

    FieldDefinition setRequired();

    Field createField();
}
