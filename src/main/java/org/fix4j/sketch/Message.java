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

import java.util.function.Consumer;
import java.util.function.Supplier;

public interface Message<T extends MessageType> {
    T messageType();

    boolean isSet(int tag);

    // I'm guessing at a minimum we need data types of:
    //      Char, String, int, long, boolean, "date" (as a long perhaps?)
    // Do we add get/set for each of these? That's a lot of methods :-O
    // Do we use generics on Tag? if so, won't that create a lot of boxing?

    String get(int tag);
    void get(int tag, Consumer<String> consumer);

    void set(int tag, String value);
    void set(int tag, Supplier<String> supplier);

    void copy(int tag, Message<T> from);
}
