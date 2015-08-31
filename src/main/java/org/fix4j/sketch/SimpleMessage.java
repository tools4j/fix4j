package org.fix4j.sketch;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;
import java.util.function.Supplier;

public final class SimpleMessage<T extends MessageType> implements Message<T> {
    private final T messageType;
    private final Field[] fields;

    public SimpleMessage(final T messageType, final Field[] fields) {
        this.messageType = Objects.requireNonNull(messageType);
        this.fields = Objects.requireNonNull(fields);
    }

    @Override
    public T messageType() {
        return messageType;
    }

    @Override
    public boolean isSet(final FieldType fieldType) {
        return false;
    }

    @Override
    public void unset(final FieldType fieldType) {

    }

    @Override
    public void set(final StringFieldType tag, final String value) {

    }

    @Override
    public void set(final StringFieldType tag, final Supplier<String> supplier) {

    }

    @Override
    public String get(final StringFieldType tag) {
        return null;
    }

    @Override
    public void get(final StringFieldType tag, final Consumer<String> consumer) {

    }

    @Override
    public void set(final IntFieldType tag, final int value) {

    }

    @Override
    public void set(final IntFieldType tag, final IntSupplier supplier) {

    }

    @Override
    public int get(final IntFieldType tag) {
        return 0;
    }

    @Override
    public void get(final IntFieldType tag, final IntConsumer consumer) {

    }
}
