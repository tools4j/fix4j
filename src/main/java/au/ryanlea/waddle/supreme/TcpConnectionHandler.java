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
import java.nio.channels.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ryan on 1/06/16.
 */
public class TcpConnectionHandler {

    private final Selector selector;

    private final List<SelectionKey> selectionKeys = new ArrayList<>();

    private final TcpExceptionHandler tcpExceptionHandler;

    private int select;

    public TcpConnectionHandler(final TcpExceptionHandler tcpExceptionHandler) throws IOException {
        this.tcpExceptionHandler = tcpExceptionHandler;
        this.selector = Selector.open();
    }

    public void register(ServerSocketChannel serverSocketChannel, FixSession fixSession) throws ClosedChannelException {
        selectionKeys.add(serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT, fixSession));
    }

    public void register(SocketChannel socketChannel, FixSession fixSession) throws ClosedChannelException {
        selectionKeys.add(socketChannel.register(selector, SelectionKey.OP_CONNECT, fixSession));
    }

    public TcpConnectionHandler selectAndConnect() {
        try {
            select = selector.selectNow();
            if (select > 0) {
                for (int i = 0; i < selectionKeys.size(); i++) {
                    final SelectionKey key = selectionKeys.get(i);
                    final FixSession fixSession = (FixSession) key.attachment();
                    if (key.isAcceptable() || key.isConnectable()) {
                        fixSession.tcpConnection().connect().socketChannel().register(selector, SelectionKey.OP_READ);
                    }
                }
            }
        } catch (IOException e) {
            tcpExceptionHandler.onError(e);
        }
        return this;
    }

}
