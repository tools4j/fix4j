/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-2017 fix4j.org (tools4j.org)
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

import static org.fix4j.engine.util.MathUtil.nextPowerOfTwo;

/**
 * Created by ryan on 12/12/16.
 */
public interface AsciiString extends CharSequence {

    @Override
    default char charAt(int index) {
        return (char) byteAt(index);
    }

    @Override
    default CharSequence subSequence(int start, int end) {
        throw new UnsupportedOperationException("not yet implemented");
    }

    byte byteAt(int index);

    class Mutable implements AsciiString {

        private byte[] bytes;

        private int pos;

        public Mutable(final int capacity) {
            pos = 0;
            bytes = new byte[nextPowerOfTwo(capacity)];
        }

        public Mutable(final CharSequence charSequence) {
            this(charSequence.length());
            append(charSequence);
        }

        /**
         * Appends the charSequence to the end of this
         *
         * @param charSequence  what to append
         * @return              this
         */
        public Mutable append(final CharSequence charSequence) {
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
        public byte byteAt(int index) {
            return bytes[index];
        }

        public void reset() {
            pos = 0;
        }

        public Mutable append(byte b) {
            bytes[pos] = b;
            pos++;
            return this;
        }

        public Mutable append(char c) {
            return append((byte)c);
        }

        public String toString() {
            return new String(bytes, 0, length());
        }
    }
}
