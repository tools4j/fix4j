package au.ryanlea.waddle.supreme;

import sun.misc.Unsafe;
import sun.nio.ch.DirectBuffer;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;

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
            int bytes = readableByteChannel.read(buffer);
            writePosition += bytes;
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
        long headerBytes = header.mark(this);
        long address = writeAddress() + headerBytes;
        int bytesToRead = buffer.bytesToRead();
        UNSAFE.copyMemory(buffer.readAddress(), address, bytesToRead);
        writePosition += headerBytes + bytesToRead;
        buffer.readSkip(bytesToRead);
        return this;
    }

    @Override
    public OffHeapBuffer readFrom(byte[] bytes, Header header) {
        long headerBytes = header.mark(this);
        long address = writeAddress() + headerBytes;
        int bytesToRead = bytes.length;
        UNSAFE.copyMemory(bytes, Unsafe.ARRAY_BYTE_BASE_OFFSET, null, address, bytesToRead);
        writePosition += headerBytes + bytesToRead;
        return this;
    }

    @Override
    public OffHeapBuffer readFrom(Buffer buffer, Header header) {
        long headerBytes = header.mark(this);
        long address = writeAddress() + headerBytes;
        long bytesToRead = buffer.remaining();
        int i;
        for (i = 0; i < bytesToRead; i++) {
            UNSAFE.putByte(address + i, buffer.getByte(i));
        }
        writePosition += headerBytes + i;
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
