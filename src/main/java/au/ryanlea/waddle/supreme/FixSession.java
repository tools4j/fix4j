package au.ryanlea.waddle.supreme;

/**
 * Created by ryan on 2/06/16.
 */
public class FixSession {

    private final TcpConnection tcpConnection;

    private final MessageLog inbound;

    private final MessageLog outbound;

    private boolean loggedOn;

    public FixSession(final TcpConnection tcpConnection, final MessageLog inbound, final MessageLog outbound) {
        this.tcpConnection = tcpConnection;
        this.inbound = inbound;
        this.outbound = outbound;
    }

    public FixSession establish() {
        tcpConnection.establish(this);
        return this;
    }

    public FixSession send(Message message) {
        outbound.readFrom(message);
        return this;
    }

    public FixSession fromWire() {
        inbound.readFrom(tcpConnection.buffer());
        return this;
    }

    public TcpConnection tcpConnection() {
        return tcpConnection;
    }

    public FixSession toWire() {
        outbound.writeTo(tcpConnection.buffer());
        return this;
    }

    public FixSession process() {
        // readFrom messages from inbound

        // add heartbeat or test request if required.
        logon();

        return this;
    }

    private void logon() {
        if (tcpConnection.isConnected() && !loggedOn) {
            send(new StringMessage("logon"));
        }
    }

}
