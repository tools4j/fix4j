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
