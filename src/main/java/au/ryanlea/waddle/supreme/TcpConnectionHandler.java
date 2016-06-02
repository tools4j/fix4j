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

    public TcpConnectionHandler fromWire() {
        if (select > 0) {
            for (int i = 0; i < selectionKeys.size(); i++) {
                final SelectionKey key = selectionKeys.get(i);
                final FixSession fixSession = (FixSession) key.attachment();
                if (key.isReadable()) {
                    fixSession.fromWire();
                }
            }
        }
        return this;
    }

    public TcpConnectionHandler toWire() {
        if (select > 0) {
            for (int i = 0; i < selectionKeys.size(); i++) {
                final SelectionKey key = selectionKeys.get(i);
                final FixSession fixSession = (FixSession) key.attachment();
                if (key.isWritable()) {
                    fixSession.toWire();
                }
            }
        }
        return this;
    }
}
