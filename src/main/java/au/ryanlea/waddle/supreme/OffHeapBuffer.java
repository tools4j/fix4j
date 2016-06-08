package au.ryanlea.waddle.supreme;

import sun.misc.Unsafe;

import java.nio.channels.ReadableByteChannel;

/**
 * Created by ryan on 6/06/16.
 */
public interface OffHeapBuffer {

    OffHeapBuffer readFrom(ReadableByteChannel readableByteChannel);

    OffHeapBuffer readFrom(OffHeapBuffer buffer, Header header);

    OffHeapBuffer readFrom(byte[] bytes, Header header);

    OffHeapBuffer readFrom(Buffer buffer, Header header);

    OffHeapBuffer writeTo(Buffer buffer);

    int bytesToRead();

    int bytesToWrite();

    boolean cas(long compare, long set);

    long readAddress();

    long writeAddress();

    OffHeapBuffer readSkip(int bytes);

    @FunctionalInterface
    interface Header {
        long mark(OffHeapBuffer offHeapBuffer);
    }
}
