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
package org.fix4j.engine.io;

import org.fix4j.engine.ExceptionHandler;
import org.tools4j.mmap.io.MessageReader;
import org.tools4j.mmap.io.MessageWriter;
import sun.misc.Unsafe;
import sun.nio.ch.DirectBuffer;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

/**
 * Created by ryan on 6/06/16.
 */
public class UnsafeBuffer implements OffHeapBuffer {

    public static final int TEN_MB = 10 * 1024 * 1024;

    private static Unsafe unsafe() {
        try {
            final Field field = Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            return (Unsafe) field.get(null);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            throw new Error("Unsafe.theUnsafe not found.", e);
        }
    }

    private static final Unsafe UNSAFE = unsafe();

    private final ExceptionHandler exceptionHandler;

    private final WriteAsAppendable waa = new WriteAsAppendable();

    private final ReadAsCharSequence racs = new ReadAsCharSequence();

    private ByteBuffer buffer;

    private long address;

    private int capacity;

    private int readPosition = 0;

    private int writePosition = 0;

    public UnsafeBuffer(final int capacity, final ExceptionHandler exceptionHandler) {
        this(ByteBuffer.allocateDirect(capacity), exceptionHandler);
    }

    public UnsafeBuffer(final ByteBuffer buffer, final ExceptionHandler exceptionHandler) {
        this.buffer = buffer;
        this.exceptionHandler = exceptionHandler;
        this.address = ((DirectBuffer)buffer).address();
        this.capacity = buffer.capacity();
    }


    @Override
    public OffHeapBuffer readFrom(ReadableByteChannel readableByteChannel) {
        try {
            buffer.position(writePosition);
            buffer.limit(capacity);
            int bytes = readableByteChannel.read(buffer);
            writePosition += bytes;
        } catch (IOException e) {
            exceptionHandler.onError(e);
        }
        return this;
    }

    @Override
    public OffHeapBuffer writeTo(WritableByteChannel writableByteChannel) {
        try {
            buffer.position(readPosition);
            buffer.limit(writePosition);
            int bytes = writableByteChannel.write(buffer);
            readPosition += bytes;
        } catch (IOException e) {
            exceptionHandler.onError(e);
        }
        return this;
    }

    @Override
    public int bytesToRead() {
        return writePosition - readPosition;
    }

    @Override
    public OffHeapBuffer readFrom(final MessageReader messageReader) {
        messageReader.getStringAscii(waa);
        return this;
    }

    private long readAddress() {
        return address + readPosition;
    }

    private long writeAddress() {
        return address + writePosition;
    }

    @Override
    public OffHeapBuffer writeTo(final MessageWriter messageWriter) {
        long bytesToWrite = bytesToRead();
        messageWriter.putStringAscii(racs).finishWriteMessage();
        readPosition += bytesToWrite;
        return this;
    }

    private final class WriteAsAppendable implements Appendable {

        @Override
        public Appendable append(final CharSequence csq) throws IOException {
            return append(csq, 0, csq.length());
        }

        @Override
        public Appendable append(final CharSequence csq, int start, int end) throws IOException {
            long address = writeAddress();
            for (int i = start; i < end; i++) {
                UNSAFE.putByte(address + i, (byte) csq.charAt(i));
            }
            int bytes = end - start;
            writePosition += bytes;
            return this;
        }

        @Override
        public Appendable append(char c) throws IOException {
            long address = writeAddress();
            UNSAFE.putByte(address, (byte) c);
            writePosition += 1;
            return this;
        }
    }

    private final class ReadAsCharSequence implements CharSequence {

        @Override
        public int length() {
            return bytesToRead();
        }

        @Override
        public char charAt(int index) {
            return (char) UNSAFE.getByte(readAddress() + index);
        }

        @Override
        public CharSequence subSequence(int start, int end) {
            throw new UnsupportedOperationException("#dontlike");
        }
    }

}
