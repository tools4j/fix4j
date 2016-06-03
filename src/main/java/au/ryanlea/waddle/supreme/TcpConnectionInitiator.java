package au.ryanlea.waddle.supreme;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

/**
 * Created by ryan on 1/06/16.
 */
public class TcpConnectionInitiator implements TcpConnection {

    private final String hostname;

    private final int port;

    private final TcpExceptionHandler tcpExceptionHandler;

    private final TcpConnectionHandler tcpConnectionHandler;

    private SocketChannel socketChannel;

    private ByteChannelBuffer byteChannelBuffer;

    public TcpConnectionInitiator(final String hostname,
                                  final int port,
                                  final TcpExceptionHandler tcpExceptionHandler,
                                  final TcpConnectionHandler tcpConnectionHandler) {
        this.hostname = hostname;
        this.port = port;
        this.tcpExceptionHandler = tcpExceptionHandler;
        this.tcpConnectionHandler = tcpConnectionHandler;
    }

    public TcpConnection establish(final FixSession fixSession) {
        if (socketChannel != null) {
            return this;
        }

        try {
            socketChannel = SocketChannel.open();
            socketChannel.configureBlocking(false);
            socketChannel.connect(new InetSocketAddress(hostname, port));
            byteChannelBuffer = new ByteChannelBuffer(socketChannel);
            tcpConnectionHandler.register(socketChannel, fixSession);
        } catch (IOException ioe) {
            tcpExceptionHandler.onError(this, ioe);
        }
        return this;
    }

    public SocketChannel socketChannel() {
        return socketChannel;
    }

    @Override
    public Buffer buffer() {
        return byteChannelBuffer;
    }

    @Override
    public boolean isConnected() {
        return socketChannel.isConnected();
    }

    public TcpConnectionInitiator connect() {
        try {
            socketChannel.finishConnect();
        } catch (IOException e) {
            tcpExceptionHandler.onError(this, e);
        }
        return this;
    }


}
