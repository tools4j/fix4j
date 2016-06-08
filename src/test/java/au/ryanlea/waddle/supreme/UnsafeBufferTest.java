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
package au.ryanlea.waddle.supreme;

import org.junit.Test;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;

/**
 * Created by ryan on 8/06/16.
 */
public class UnsafeBufferTest {

    @Test
    public void readUnsafeBuffer() {
        final UnsafeBuffer reader = UnsafeBuffer.wrap(ByteBuffer.allocateDirect(1024));
        final UnsafeBuffer writer = UnsafeBuffer.wrap(ByteBuffer.allocateDirect(1024));

        writer.readFrom("This is the first message".getBytes(), offHeapBuffer -> 0);
        reader.readFrom(writer, offHeapBuffer -> 0);

        final Buffer dump = new Buffer() {

            final StringBuilder sb = new StringBuilder();

            @Override
            public long remaining() {
                return 0;
            }

            @Override
            public byte getByte(int idx) {
                return 0;
            }

            @Override
            public Buffer putByte(byte b) {
                sb.append((char)b);
                return this;
            }

            @Override
            public String toString() {
                return sb.toString();
            }
        };

        reader.writeTo(dump);

        LoggerFactory.getLogger(UnsafeBufferTest.class).info(dump.toString());
    }

}