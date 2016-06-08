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

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Created by ryan on 3/06/16.
 */
public class UnsafeMessageLog implements MessageLog {

    private final String path;

    private MappedByteBuffer mbb;

    private OffHeapBuffer buffer;

    public UnsafeMessageLog(final String path, final long size, final ExceptionHandler exceptionHandler) {
        this.path = path;
        try {
            RandomAccessFile raf = new RandomAccessFile(path, "rw");
            mbb = raf.getChannel().map(FileChannel.MapMode.READ_WRITE, 0, size);
            buffer = UnsafeBuffer.wrap(mbb);
        } catch (IOException e) {
            exceptionHandler.onError(e);
        }
    }

    @Override
    public MessageLog readFrom(Buffer buffer) {
        this.buffer.readFrom(buffer, this::markStart);
        return this;
    }

    @Override
    public MessageLog writeTo(Buffer buffer) {

        return this;
    }

    @Override
    public MessageLog readFrom(OffHeapBuffer buffer) {
        this.buffer.readFrom(buffer, this::markStart);
        return this;
    }

    @Override
    public MessageLog writeTo(OffHeapBuffer buffer) {
        return null;
    }

    public UnsafeMessageLog log(byte[] bytes) {
        this.buffer.readFrom(bytes, this::markStart);
        return this;
    }

    private long markStart(final OffHeapBuffer buffer) {
        long pos;
        while ((pos = tryMarkStart(buffer)) == 0);
        return pos;
    }

    private long tryMarkStart(final OffHeapBuffer buffer) {
        if (!buffer.cas(0, System.currentTimeMillis())) {
            return 0;
        }
        return 4;
    }

}
