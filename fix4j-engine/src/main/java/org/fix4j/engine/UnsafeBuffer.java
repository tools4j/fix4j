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
package org.fix4j.engine;

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
            //
        }
        return null;
    }

    private static final Unsafe UNSAFE = unsafe();

    private final ExceptionHandler exceptionHandler;

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


    public static UnsafeBuffer wrap(ByteBuffer byteBuffer) {
        return new UnsafeBuffer(byteBuffer, ExceptionHandler.throwing());
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
    public int bytesToWrite() {
        return capacity - writePosition;
    }

    public long writeAddress() {
        return address + writePosition;
    }

    @Override
    public OffHeapBuffer readSkip(int bytes) {
        readPosition += bytes;
        return this;
    }

    public boolean cas(final long compare, final long set) {
        return UNSAFE.compareAndSwapLong(null, writeAddress(), compare, set);
    }

    @Override
    public long readAddress() {
        return address + readPosition;
    }

    @Override
    public OffHeapBuffer readFrom(final OffHeapBuffer buffer, final Header header) {
        int bytesToRead = buffer.bytesToRead();
        if (bytesToRead > 0) {
            long headerBytes = header.mark(this);
            long address = writeAddress() + headerBytes;
            UNSAFE.copyMemory(buffer.readAddress(), address, bytesToRead);
            writePosition += headerBytes + bytesToRead;
            buffer.readSkip(bytesToRead);
        }
        return this;
    }

    @Override
    public OffHeapBuffer readFrom(byte[] bytes, Header header) {
        int bytesToRead = bytes.length;
        if (bytesToRead > 0) {
            long headerBytes = header.mark(this);
            long address = writeAddress() + headerBytes;
            UNSAFE.copyMemory(bytes, Unsafe.ARRAY_BYTE_BASE_OFFSET, null, address, bytesToRead);
            writePosition += headerBytes + bytesToRead;
        }
        return this;
    }

    @Override
    public OffHeapBuffer readFrom(Buffer buffer, Header header) {
        long bytesToRead = buffer.remaining();
        if (bytesToRead > 0) {
            long headerBytes = header.mark(this);
            long address = writeAddress() + headerBytes;
            int i;
            for (i = 0; i < bytesToRead; i++) {
                UNSAFE.putByte(address + i, buffer.getByte(i));
            }
            writePosition += headerBytes + i;
        }
        return this;
    }

    @Override
    public OffHeapBuffer writeTo(Buffer buffer) {
        // todo handle a header - do we want to write it?
        long address = readAddress();
        long bytesToWrite = bytesToRead();
        int i;
        for (i = 0; i < bytesToWrite; i++) {
            buffer.putByte(UNSAFE.getByte(address + i));
        }
        writePosition += i;
        return null;
    }

}
