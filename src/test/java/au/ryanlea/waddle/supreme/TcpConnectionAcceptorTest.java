package au.ryanlea.waddle.supreme;

import org.junit.Test;

import java.io.IOException;

/**
 * Created by ryan on 1/06/16.
 */
public class TcpConnectionAcceptorTest {

    @Test
    public void establish() throws IOException {
        final TcpExceptionHandler throwing = TcpExceptionHandler.throwing();
        final TcpConnectionHandler tcpConnectionHandler = new TcpConnectionHandler(throwing);
        final TcpConnectionAcceptor tcpConnectionAcceptor = new TcpConnectionAcceptor("localhost", 0, throwing, tcpConnectionHandler);
        tcpConnectionAcceptor.establish(null);
    }

}