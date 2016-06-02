package au.ryanlea.waddle.supreme;

import java.nio.channels.SocketChannel;

/**
 * Created by ryan on 1/06/16.
 */
public interface TcpConnection {

    TcpConnection establish(FixSession fixSession);

    TcpConnection connect();

    SocketChannel socketChannel();

}
