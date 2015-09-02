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
