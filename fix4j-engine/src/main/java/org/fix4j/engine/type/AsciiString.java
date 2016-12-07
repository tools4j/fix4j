/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 fix4j.org (tools4j.org)
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
package org.fix4j.engine.type;

import org.fix4j.engine.Buffer;

import static org.fix4j.engine.util.MathUtil.nextPowerOfTwo;

public class AsciiString implements CharSequence, Buffer {

    private byte[] bytes;

    private int pos;

    public AsciiString(final int capacity) {
        pos = 0;
        bytes = new byte[nextPowerOfTwo(capacity)];
    }

    public AsciiString(final CharSequence charSequence) {
        this(charSequence.length());
        append(charSequence);
    }

    /**
     * Appends the charSequence to the end of this
     *
     * @param charSequence  what to append
     * @return              this
     */
    public AsciiString append(final CharSequence charSequence) {
        for (int i = 0; i < charSequence.length(); i++) {
            append((byte) charSequence.charAt(i));
        }
        return this;
    }

    @Override
    public int length() {
        return pos;
    }

    @Override
    public byte getByte(int idx) {
        return bytes[idx];
    }

    @Override
    public Buffer putByte(byte b) {
        return append(b);
    }

    @Override
    public char charAt(int index) {
        return (char) getByte(index);
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        throw new UnsupportedOperationException("not yet implemented");
    }

    public void reset() {
        pos = 0;
    }

    public AsciiString append(byte b) {
        bytes[pos] = b;
        pos++;
        return this;
    }

    public AsciiString append(char c) {
        return append((byte)c);
    }
}
