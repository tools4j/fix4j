package au.ryanlea.waddle.supreme;

import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

/**
 * Created by ryan on 2/06/16.
 */
public interface MessageLog {

    MessageLog read(ReadableByteChannel readableByteChannel);

    MessageLog write(WritableByteChannel writableByteChannel);
}
