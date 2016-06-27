package au.ryanlea.waddle.supreme.session;

import au.ryanlea.waddle.supreme.Application;
import au.ryanlea.waddle.supreme.log.MessageLog;
import au.ryanlea.waddle.supreme.net.SocketConnection;
import au.ryanlea.waddle.supreme.net.TcpConnectionAcceptor;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Created by ryan on 22/06/16.
 */
public class FixSessionAcceptor implements FixSessionConnection {

    private final TcpConnectionAcceptor tcpConnection;

    private final MessageLog inbound;

    private final MessageLog outbound;

    private final Supplier<SessionLifecycle> sessionLifecycleSupplier;

    private final Supplier<Application> applicationSupplier;

    public FixSessionAcceptor(final TcpConnectionAcceptor tcpConnection,
                              final MessageLog inbound,
                              final MessageLog outbound,
                              final Supplier<SessionLifecycle> sessionLifecycleSupplier,
                              final Supplier<Application> applicationSupplier) {
        this.tcpConnection = tcpConnection;
        this.inbound = inbound;
        this.outbound = outbound;
        this.sessionLifecycleSupplier = sessionLifecycleSupplier;
        this.applicationSupplier = applicationSupplier;
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
        onFixSession.accept(new FixSession(
                socketConnection,
                sessionLifecycleSupplier.get(),
                applicationSupplier.get(),
                inbound,
                outbound));
        return this;
    }

}
