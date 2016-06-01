package au.ryanlea.waddle.supreme;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/**
 * Created by ryan on 1/06/16.
 */
public class FixSessionConnectionAcceptor implements FixSessionConnection {

    private String hostname;

    private int port;

    private ServerSocketChannel serverSocketChannel;

    private SocketChannel socketChannel;

    private final TcpExceptionHandler tcpExceptionHandler;

    public FixSessionConnectionAcceptor(final String hostname, final int port, final TcpExceptionHandler tcpExceptionHandler) {
        this.hostname = hostname;
        this.port = port;
        this.tcpExceptionHandler = tcpExceptionHandler;
    }

    public FixSessionConnection establish(TcpConnectionHandler tcpConnectionHandler) {
        if (serverSocketChannel != null) {
            return this;
        }

        try {
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.socket().bind(new InetSocketAddress(hostname, port));
            serverSocketChannel.configureBlocking(false);

            tcpConnectionHandler.register(this);
        } catch (IOException e) {
            tcpExceptionHandler.onError(this, e);
        }

        return this;
    }

    @Override
    public FixSessionConnection action() {
        return this;
    }

    public ServerSocketChannel serverSocketChannel() {
        return serverSocketChannel;
    }

    public FixSessionConnectionAcceptor connect() {
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
