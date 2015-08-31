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
    public void copy(final int tag, final Message<T> from) {
    }
}
