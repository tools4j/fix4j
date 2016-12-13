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
package org.fix4j.engine.util;

import org.fix4j.engine.type.AsciiString;

/**
 * Created by ryan on 5/12/16.
 */
public class EncodeUtil {

    private static final ThreadLocal<StringBuilder> checksumTL = new ThreadLocal<StringBuilder>() {
        @Override
        protected StringBuilder initialValue() {
            return new StringBuilder(3);
        }
    };

    public static CharSequence generateChecksum(
            final AsciiString header,
            final AsciiString message,
            final AsciiString trailer
    ) {
        final StringBuilder sb = checksumTL.get();
        sb.setLength(0);
        long checksum = 0;
        for (int i = 0; i < header.length(); i++) {
            checksum += header.byteAt(i);
        }

        for (int i = 0; i < message.length(); i++) {
            checksum += message.byteAt(i);
        }

        for (int i = 0; i < trailer.length(); i++) {
            checksum += trailer.byteAt(i);
        }

        checksum %= 256;

        if (checksum < 100) {
            sb.append('0');
        }
        if (checksum < 10) {
            sb.append('0');
        }
        sb.append(checksum);
        return sb;
    }


}
