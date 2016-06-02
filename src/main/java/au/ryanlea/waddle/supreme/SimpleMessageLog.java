package au.ryanlea.waddle.supreme;

import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.WritableByteChannel;

/**
 * Created by ryan on 2/06/16.
 */
public class SimpleMessageLog implements MessageLog {

    private final String path;

    private final ByteBuffer buffer = ByteBuffer.allocateDirect(16384);

    public SimpleMessageLog(String path) {
        this.path = path;
    }

    @Override
    public MessageLog read(ReadableByteChannel readableByteChannel) {
        return null;
    }

    @Override
    public MessageLog write(WritableByteChannel writableByteChannel) {
        return this;
    }
}
