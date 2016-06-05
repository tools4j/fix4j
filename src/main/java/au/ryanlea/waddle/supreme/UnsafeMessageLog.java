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

import sun.misc.Unsafe;
import sun.nio.ch.DirectBuffer;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.Field;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Created by ryan on 3/06/16.
 */
public class UnsafeMessageLog implements MessageLog {

    private static final Unsafe UNSAFE = unsafe();

    private final String path;

    private MappedByteBuffer mbb;

    private long address;

    private long position;

    private long size;

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

    public UnsafeMessageLog(final String path, final long size) {
        this.path = path;
        try {
            RandomAccessFile raf = new RandomAccessFile(path, "rw");
            mbb = raf.getChannel().map(FileChannel.MapMode.READ_WRITE, 0, size);
            address = ((DirectBuffer)mbb).address();
            position = 0;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public MessageLog readFrom(Buffer buffer) {
        long pos = markStart();
        long address = currentAddress() + pos;
        final long length = buffer.remaining();
        for (int i = 0; i < length; i++, pos++) {
            UNSAFE.putByte(address + i, buffer.getByte(i));
        }
        position += pos;
        return this;
    }

    @Override
    public MessageLog writeTo(Buffer buffer) {

        return this;
    }

    public UnsafeMessageLog log(String message) {
        long pos = markStart();
        long address = currentAddress() + pos;

        final byte[] bytes = message.getBytes();
        int length = bytes.length;
        for (int i = 0; i < length; i++, pos++) {
            UNSAFE.putByte(address + i, bytes[i]);
        }
        position += pos;
        return this;
    }

    public UnsafeMessageLog log(byte[] bytes) {
        long pos = markStart();
        long address = currentAddress() + pos;

        int length = bytes.length;
        for (int i = 0; i < length; i++, pos++) {
            UNSAFE.putByte(address + i, bytes[i]);
        }
        position += pos;
        return this;
    }

    private long markStart() {
        long pos;
        while ((pos = tryMarkStart()) == 0);
        return pos;
    }

    private long currentAddress() {
        return address + position;
    }

    private long tryMarkStart() {
        if (!UNSAFE.compareAndSwapLong(null, currentAddress(), 0, System.currentTimeMillis())) {
            return 0;
        }
        return 4;
    }

}
