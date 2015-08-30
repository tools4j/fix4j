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
package org.fix4j.engine.msg;

import java.util.function.Consumer;
import java.util.function.DoubleConsumer;
import java.util.function.IntConsumer;
import java.util.function.LongConsumer;
import java.util.function.LongSupplier;
import java.util.function.Supplier;

import org.fix4j.engine.tag.DoubleTag;
import org.fix4j.engine.tag.FixTag;
import org.fix4j.engine.tag.LongTag;
import org.fix4j.engine.tag.ObjectTag;

public interface TagBag {
    boolean isSet(FixTag tag);

    String getString(FixTag tag);
    void getString(FixTag tag, Consumer<? super String> consumer);
    long getInt(LongTag tag);
    void getInt(LongTag tag, IntConsumer consumer);
    long getLong(LongTag tag);
    void getLong(LongTag tag, LongConsumer consumer);
    double getDouble(DoubleTag tag);
    void getDouble(DoubleTag tag, DoubleConsumer consumer);
    <T> T getObject(ObjectTag<T> tag);
    <T> void getObject(ObjectTag<T> tag, Consumer<? super T> consumer);

    void setString(FixTag tag, String value);
    void setString(FixTag tag, Supplier<? extends String> supplier);
    void setLong(LongTag tag, long value);
    void setLong(LongTag tag, LongSupplier supplier);
    <T> void setObject(ObjectTag<T> tag, T value);
    <T> void setObject(ObjectTag<T> tag, Supplier<? extends T> supplier);

    void copy(FixTag tag, TagBag from);
}
