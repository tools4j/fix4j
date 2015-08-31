package org.fix4j.sketch;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

public final class SimpleDictionary<T extends MessageType> implements Dictionary<T> {
    private final Map<T, SimpleMessageDefinition<T>> messageDefinitions = new HashMap<>();

    public static <T extends MessageType> SimpleDictionary<T> define() {
        return new SimpleDictionary<>();
    }

    public static <T extends MessageType, F extends FieldType> SimpleDictionary<T> define(final Consumer<Dictionary<T>> consumer) {
        final SimpleDictionary<T> dictionary = new SimpleDictionary<>();
        consumer.accept(dictionary);
        return dictionary;
    }

    private SimpleDictionary() {
    }

    @Override
    public SimpleMessageDefinition<T> defineMessage(final T messageType) {
        Objects.requireNonNull(messageType);
        return messageDefinitions.computeIfAbsent(messageType, SimpleMessageDefinition::new);
    }

    @Override
    public SimpleMessageDefinition<T> defineMessage(final T messageType, final Consumer<MessageDefinition<T>> consumer) {
        final SimpleMessageDefinition<T> messageDefinition = defineMessage(messageType);
        consumer.accept(messageDefinition);
        return messageDefinition;
    }

    @Override
    public SimpleMessage<T> createMessage(final T messageType) {
        return messageDefinition(messageType).createMessage();
    }

    @Override
    public SimpleMessage<T> createMessage(final T messageType, final Consumer<Message<T>> consumer) {
        return messageDefinition(messageType).createMessage(consumer);
    }

    private SimpleMessageDefinition<T> messageDefinition(final T messageType) {
        return messageDefinitions.get(messageType);
    }
}
