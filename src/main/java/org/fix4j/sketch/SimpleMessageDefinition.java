package org.fix4j.sketch;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

public final class SimpleMessageDefinition<T extends MessageType> implements MessageDefinition<T> {
    private final Map<FieldType, SimpleFieldDefinition> fieldDefinitions = new HashMap<>();
    private final T messageType;

    public SimpleMessageDefinition(final T messageType) {
        this.messageType = Objects.requireNonNull(messageType);
    }

    @Override
    public SimpleFieldDefinition defineField(final FieldType fieldType) {
        Objects.requireNonNull(fieldType);
        return fieldDefinitions.computeIfAbsent(fieldType, SimpleFieldDefinition::new);
    }

    @Override
    public SimpleFieldDefinition defineField(final FieldType fieldType, final Consumer<FieldDefinition> consumer) {
        final SimpleFieldDefinition fieldDefinition = defineField(fieldType);
        consumer.accept(fieldDefinition);
        return fieldDefinition;
    }

    @Override
    public SimpleMessage<T> createMessage() {
        final Field[] fields = fieldDefinitions
                .values()
                .stream()
                .map(FieldDefinition::createField)
                .toArray(Field[]::new);
        return new SimpleMessage<>(messageType, fields);
    }

    @Override
    public SimpleMessage<T> createMessage(final Consumer<Message<T>> consumer) {
        final SimpleMessage<T> message = createMessage();
        consumer.accept(message);
        return message;
    }
}
