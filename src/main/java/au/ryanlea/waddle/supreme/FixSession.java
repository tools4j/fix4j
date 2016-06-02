package au.ryanlea.waddle.supreme;

import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * Created by ryan on 2/06/16.
 */
public class FixSession {

    private final TcpConnection tcpConnection;

    private final MessageLog inbound;

    private final MessageLog outbound;

    public FixSession(final TcpConnection tcpConnection, final MessageLog inbound, final MessageLog outbound) {
        this.tcpConnection = tcpConnection;
        this.inbound = inbound;
        this.outbound = outbound;
    }

    public FixSession establish() {
        tcpConnection.establish(this);
        return this;
    }

    public FixSession send(String message) {

        return this;
    }

    public FixSession fromWire() {
        inbound.read(tcpConnection.socketChannel());
        return this;
    }

    public TcpConnection tcpConnection() {
        return tcpConnection;
    }

    public FixSession toWire() {
        outbound.write(tcpConnection.socketChannel());
        return this;
    }

    public FixSession process() {
        // read messages from inbound

        // add heartbeat or test request if required.

        return this;
    }
}
