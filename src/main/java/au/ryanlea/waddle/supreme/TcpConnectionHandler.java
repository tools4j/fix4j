package au.ryanlea.waddle.supreme;

import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ryan on 1/06/16.
 */
public class TcpConnectionHandler {

    private final Selector selector;

    private final List<SelectionKey> acceptKeys = new ArrayList<>();

    private final List<SelectionKey> initiateKeys = new ArrayList<>();

    private final TcpExceptionHandler tcpExceptionHandler;

    public TcpConnectionHandler(final TcpExceptionHandler tcpExceptionHandler) throws IOException {
        this.tcpExceptionHandler = tcpExceptionHandler;
        this.selector = Selector.open();
    }

    public void register(FixSessionConnectionAcceptor fixSessionAcceptor) throws ClosedChannelException {
        acceptKeys.add(fixSessionAcceptor.serverSocketChannel().register(selector, SelectionKey.OP_ACCEPT, fixSessionAcceptor));
    }

    public void register(FixSessionConnectionInitiator fixSessionInitiator) throws ClosedChannelException {
        initiateKeys.add(fixSessionInitiator.socketChannel().register(selector, SelectionKey.OP_CONNECT, fixSessionInitiator));
    }

    public void handle() {
        try {
            if (selector.selectNow() > 0) {
                for (int i = 0; i < acceptKeys.size(); i++) {
                    final SelectionKey key = acceptKeys.get(i);
                    if (key.isAcceptable()) {
                        final FixSessionConnectionAcceptor fixSessionAcceptor = (FixSessionConnectionAcceptor) key.attachment();
                        fixSessionAcceptor.connect().socketChannel().register(selector, SelectionKey.OP_READ);
                    }
                }
                for (int i = 0; i < initiateKeys.size(); i++) {
                    final SelectionKey key = initiateKeys.get(i);
                    if (key.isConnectable()) {
                        final FixSessionConnectionInitiator fixSessionInitiator = (FixSessionConnectionInitiator) key.attachment();
                        fixSessionInitiator.connect();
                        key.interestOps(SelectionKey.OP_READ | SelectionKey.OP_WRITE);
                    }
                }
            }
        } catch (IOException e) {
            tcpExceptionHandler.onError(e);
        }
    }
}
