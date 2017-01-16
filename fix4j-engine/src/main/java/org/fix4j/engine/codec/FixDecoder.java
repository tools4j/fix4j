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
package org.fix4j.engine.codec;

import org.fix4j.engine.type.AsciiString;

import java.io.IOException;

/**
 * Created by ryan on 12/12/16.
 */
public class FixDecoder implements TagDecoder, ValueDecoder {

    public static final char FIELD_DELIMETER = '\u0001';
    public static final char FIELD_SEPARATOR = '=';
    private AsciiString content;

    private int tag;

    private int index;

    public TagDecoder wrap(final AsciiString content) {
        this.content = content;
        index = 0;
        return this;
    }

    @Override
    public TagDecoder tag(final ValueHandler valueHandler) {
        moveToTagStart();
        int tag = 0;
        byte c;
        for (int i = 0; index < content.length(); i++) {
            c = content.byteAt(index++);
            if (c == FIELD_SEPARATOR) {
                break;
            }
            tag = (tag * 10) + (c - '0');
        }
        valueHandler.handle(tag, this);
        return this;
    }

    @Override
    public boolean hasNext() {
        return index < content.length();
    }

    private void moveToTagStart() {
        if (index == 0) {
            return;
        }
        byte b = content.byteAt(index++);
        while (b != FIELD_DELIMETER) {
            b = content.byteAt(index++);
        }
    }

    @Override
    public void getString(Appendable appendable) {
        byte c;
        for (int i = 0; index < content.length(); i++) {
            c = content.byteAt(index);
            if (c == FIELD_DELIMETER) {
                break;
            }
            index++;
            try {
                appendable.append((char) c);
            } catch (IOException e) {
                // nothing right now.
            }
        }
    }

    @Override
    public void getString(final AsciiString.Mutable asciiString) {
        byte c;
        for (int i = 0; index < content.length(); i++) {
            c = content.byteAt(index);
            if (c == FIELD_DELIMETER) {
                break;
            }
            index++;
            asciiString.append((char) c);
        }
    }

    @Override
    public int getInt() {
        int result = 0;
        byte c;
        for (int i = 0; index < content.length(); i++) {
            c = content.byteAt(index);
            if (c == FIELD_DELIMETER) {
                break;
            }
            index++;
            result = result * 10 + (c - '0');
        }
        return result;
    }
}
