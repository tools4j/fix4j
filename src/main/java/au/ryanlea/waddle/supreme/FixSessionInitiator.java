package au.ryanlea.waddle.supreme;

/**
 * Created by ryan on 3/06/16.
 */
public class FixSessionInitiator extends FixSession {

    public FixSessionInitiator(final TcpConnectionInitiator tcpConnection,
                               final MessageLog inbound,
                               final MessageLog outbound) {
        super(tcpConnection, inbound, outbound);
    }

}
