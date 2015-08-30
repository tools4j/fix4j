package org.fix4j.sketch;

import java.util.Objects;

public final class Dictionary<T extends MessageType> {
    private final Class<T> klass;
    private final T heartbeatMessageType;

    public static <T extends MessageType> Dictionary<T> of(final Class<T> klass) {
        for (final T messageType : klass.getEnumConstants()) {
            if ("0".equals(messageType.tagValue())) {
                return new Dictionary<>(klass, messageType);
            }
        }
        throw new IllegalArgumentException("MessageType doesn't define a heartbeat");
    }

    private Dictionary(final Class<T> klass, final T heartbeatMessageType) {
        this.klass = klass;
        this.heartbeatMessageType = Objects.requireNonNull(heartbeatMessageType);
    }

    public Message<T> parse(final String fix) {
        return NullMessage.of(heartbeatMessageType);
    }
}
