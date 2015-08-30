package org.fix4j.sketch;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

public final class NullMessage<T extends MessageType> implements Message<T> {
    private final T messageType;

    public static <T extends MessageType> NullMessage<T> of(final T messageType) {
        return new NullMessage<>(messageType);
    }

    private NullMessage(final T messageType) {
        this.messageType = Objects.requireNonNull(messageType);
    }

    @Override
    public T messageType() {
        return messageType;
    }

    @Override
    public boolean isSet(final int tag) {
        return false;
    }

    @Override
    public String get(final int tag) {
        throw new IllegalArgumentException();
    }

    @Override
    public void get(final int tag, final Consumer<String> consumer) {
    }

    @Override
    public void set(final int tag, final String value) {
    }

    @Override
    public void set(final int tag, final Supplier<String> supplier) {
    }

    @Override
    public void copy(final int tag, final Message from) {
    }
}
