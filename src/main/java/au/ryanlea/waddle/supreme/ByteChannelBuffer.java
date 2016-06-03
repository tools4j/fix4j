package au.ryanlea.waddle.supreme;

import java.nio.channels.ByteChannel;

/**
 * Created by ryan on 3/06/16.
 */
public class ByteChannelBuffer implements Buffer {

    private final ByteChannel byteChannel;

    public ByteChannelBuffer(ByteChannel byteChannel) {
        this.byteChannel = byteChannel;
    }

    @Override
    public long remaining() {
        return 0;
    }

    @Override
    public byte getByte(int pos) {
        return 0;
    }
}
