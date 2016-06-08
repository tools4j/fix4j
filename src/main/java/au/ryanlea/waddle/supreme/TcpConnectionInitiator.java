/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 fix4j.org (tools4j.org)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
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
    public boolean isConnected() {
        return socketChannel.isConnected();
    }

    @Override
    public TcpConnection readInto(MessageLog messageLog) {
        return this;
    }

    @Override
    public TcpConnection writeFrom(MessageLog messageLog) {
        return this;
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
