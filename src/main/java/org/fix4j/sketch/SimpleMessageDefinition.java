/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 fix4j.org (tools4j.org)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
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
