package au.ryanlea.waddle.supreme;

import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.in;
import static org.mockito.Mockito.mock;

/**
 * Created by ryan on 1/06/16.
 */
public class FixEngineTest {

    @Test
    public void registerSession() throws IOException {
        final TcpExceptionHandler throwing = TcpExceptionHandler.throwing();
        final TcpConnectionHandler tcpConnectionHandler = new TcpConnectionHandler(throwing);
        final FixEngine fixEngine = new FixEngine(tcpConnectionHandler, ExceptionHandler.logging());
        final FixSessionConnectionAcceptor fixSessionAcceptor = new FixSessionConnectionAcceptor("localhost", 0, throwing);
        fixEngine.register(fixSessionAcceptor);
        assertThat(fixEngine.acceptorFixSessions()).contains(fixSessionAcceptor);
    }

    @Test
    public void start() throws IOException {
        final TcpExceptionHandler tcpExceptionHandler = TcpExceptionHandler.throwing();
        final TcpConnectionHandler tcpConnectionHandler = new TcpConnectionHandler(tcpExceptionHandler);
        final FixEngine fixEngine = new FixEngine(tcpConnectionHandler, ExceptionHandler.logging());
        fixEngine.start();

        final FixSessionConnectionAcceptor acceptor = new FixSessionConnectionAcceptor("localhost", 0, tcpExceptionHandler);
        fixEngine.register(acceptor);

        while (boundPort(acceptor) <= 0);

        final FixSessionConnectionInitiator initiator = new FixSessionConnectionInitiator("localhost", acceptor.serverSocketChannel().socket().getLocalPort(), tcpExceptionHandler);
        fixEngine.register(initiator);

        fixEngine.terminate(120, TimeUnit.SECONDS);
    }

    private int boundPort(FixSessionConnectionAcceptor acceptor) {
        if (acceptor.serverSocketChannel() == null) {
            return -1;
        }

        return acceptor.serverSocketChannel().socket().getLocalPort();
    }

}
