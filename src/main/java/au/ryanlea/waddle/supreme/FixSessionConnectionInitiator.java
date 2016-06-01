package au.ryanlea.waddle.supreme;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

/**
 * Created by ryan on 1/06/16.
 */
public class FixSessionConnectionInitiator implements FixSessionConnection {

    private final TcpExceptionHandler tcpExceptionHandler;

    private final String hostname;

    private final int port;

    private SocketChannel socketChannel;

    public FixSessionConnectionInitiator(final String hostname, final int port, final TcpExceptionHandler tcpExceptionHandler) {
        this.hostname = hostname;
        this.port = port;
        this.tcpExceptionHandler = tcpExceptionHandler;
    }

    public FixSessionConnection establish(final TcpConnectionHandler tcpConnectionHandler) {
        if (socketChannel != null) {
            return this;
        }

        try {
            socketChannel = SocketChannel.open();
            socketChannel.configureBlocking(false);
            socketChannel.connect(new InetSocketAddress(hostname, port));
            tcpConnectionHandler.register(this);
        } catch (IOException ioe) {
            tcpExceptionHandler.onError(this, ioe);
        }
        return this;
    }

    @Override
    public FixSessionConnection action() {
        return this;
    }

    public SocketChannel socketChannel() {
        return socketChannel;
    }

    public FixSessionConnectionInitiator connect() {
        return this;
    }
}
