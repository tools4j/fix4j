package au.ryanlea.waddle.supreme;

/**
 * Created by ryan on 1/06/16.
 */
public interface FixSessionConnection {
    FixSessionConnection establish(TcpConnectionHandler tcpConnectionHandler);

    FixSessionConnection action();
}
