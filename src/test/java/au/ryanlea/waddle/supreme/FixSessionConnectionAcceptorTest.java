package au.ryanlea.waddle.supreme;

import org.junit.Test;

import java.io.IOException;

/**
 * Created by ryan on 1/06/16.
 */
public class FixSessionConnectionAcceptorTest {

    @Test
    public void establish() throws IOException {
        final TcpExceptionHandler throwing = TcpExceptionHandler.throwing();
        final FixSessionConnectionAcceptor fixSessionAcceptor = new FixSessionConnectionAcceptor("localhost", 0, throwing);
        final TcpConnectionHandler tcpConnectionHandler = new TcpConnectionHandler(throwing);
        fixSessionAcceptor.establish(tcpConnectionHandler);
    }

}