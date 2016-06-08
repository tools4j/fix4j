/**
 * The MIT License (MIT)
 * <p>
 * Copyright (c) 2016 fix4j.org (tools4j.org)
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
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

    private final OffHeapBuffer offHeapBuffer;

    public TcpConnectionAcceptor(final String hostname,
                                 final int port,
                                 final TcpExceptionHandler tcpExceptionHandler,
                                 final TcpConnectionHandler tcpConnectionHandler) {
        this.hostname = hostname;
        this.port = port;
        this.tcpExceptionHandler = tcpExceptionHandler;
        this.tcpConnectionHandler = tcpConnectionHandler;
        this.offHeapBuffer = new UnsafeBuffer(UnsafeBuffer.TEN_MB, tcpExceptionHandler);
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

    @Override
    public boolean isConnected() {
        return socketChannel != null && socketChannel.isConnected();
    }

    @Override
    public TcpConnection readInto(MessageLog messageLog) {
        offHeapBuffer.readFrom(socketChannel);
        if (offHeapBuffer.bytesToWrite() > 0) {
            messageLog.readFrom(offHeapBuffer);
        }
        return this;
    }

    @Override
    public TcpConnection writeFrom(MessageLog messageLog) {
        return this;
    }
}
