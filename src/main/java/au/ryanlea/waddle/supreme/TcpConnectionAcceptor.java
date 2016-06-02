package au.ryanlea.waddle.supreme;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/**
 * Created by ryan on 1/06/16.
 */
public class TcpConnectionAcceptor implements TcpConnection {

    private final String hostname;

    private final int port;

    private final TcpExceptionHandler tcpExceptionHandler;

    private final TcpConnectionHandler tcpConnectionHandler;

    private ServerSocketChannel serverSocketChannel;

    private SocketChannel socketChannel;

    public TcpConnectionAcceptor(final String hostname,
                                 final int port,
                                 final TcpExceptionHandler tcpExceptionHandler,
                                 final TcpConnectionHandler tcpConnectionHandler) {
        this.hostname = hostname;
        this.port = port;
        this.tcpExceptionHandler = tcpExceptionHandler;
        this.tcpConnectionHandler = tcpConnectionHandler;
    }

    public TcpConnection establish(final FixSession fixSession) {
        if (serverSocketChannel != null) {
            return this;
        }

        try {
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.socket().bind(new InetSocketAddress(hostname, port));
            serverSocketChannel.configureBlocking(false);

            tcpConnectionHandler.register(serverSocketChannel, fixSession);
        } catch (IOException e) {
            tcpExceptionHandler.onError(this, e);
        }

        return this;
    }

    public ServerSocketChannel serverSocketChannel() {
        return serverSocketChannel;
    }

    public TcpConnectionAcceptor connect() {
        try {
            socketChannel = serverSocketChannel.accept();
            socketChannel.configureBlocking(false);
        } catch (IOException e) {
            tcpExceptionHandler.onError(this, e);
        }
        return this;
    }

    public SocketChannel socketChannel() {
        return socketChannel;
    }
}
