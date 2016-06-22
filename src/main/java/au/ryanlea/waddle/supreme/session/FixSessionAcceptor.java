package au.ryanlea.waddle.supreme.session;

import au.ryanlea.waddle.supreme.FixEngine;
import au.ryanlea.waddle.supreme.MessageLog;
import au.ryanlea.waddle.supreme.net.SocketConnection;
import au.ryanlea.waddle.supreme.net.TcpConnectionAcceptor;

import java.util.function.Consumer;

/**
 * Created by ryan on 22/06/16.
 */
public class FixSessionAcceptor implements FixSessionConnection {

    private final TcpConnectionAcceptor tcpConnection;

    private final FixEngine fixEngine;

    private final MessageLog inbound;

    private final MessageLog outbound;

    public FixSessionAcceptor(final TcpConnectionAcceptor tcpConnection,
                              final FixEngine fixEngine,
                              final MessageLog inbound,
                              final MessageLog outbound) {
        this.tcpConnection = tcpConnection;
        this.fixEngine = fixEngine;
        this.inbound = inbound;
        this.outbound = outbound;
    }

    @Override
    public FixSessionConnection establish() {
        tcpConnection.establish(this);
        return this;
    }

    @Override
    public FixSessionConnection connect(Consumer<FixSession> onFixSession) {
        final SocketConnection socketConnection = tcpConnection.connect();
        // todo - inbound and outbound need to be constructed based upon something - or anything
        onFixSession.accept(new FixSession(socketConnection, inbound, outbound));
        return this;
    }
}
