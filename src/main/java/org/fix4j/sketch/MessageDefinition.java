package org.fix4j.sketch;

import java.util.function.Consumer;

public interface MessageDefinition<T extends MessageType> {
    FieldDefinition defineField(FieldType fieldType);

    FieldDefinition defineField(FieldType fieldType, Consumer<FieldDefinition> consumer);

    Message<T> createMessage();

    Message<T> createMessage(Consumer<Message<T>> consumer);
}
