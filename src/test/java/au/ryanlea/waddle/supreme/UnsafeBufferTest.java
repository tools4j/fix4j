package au.ryanlea.waddle.supreme;

import org.junit.Test;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;

import static org.junit.Assert.*;

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