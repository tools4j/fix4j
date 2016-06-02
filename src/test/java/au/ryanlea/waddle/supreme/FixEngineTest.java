package au.ryanlea.waddle.supreme;

import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by ryan on 1/06/16.
 */
public class FixEngineTest {

    @Test
    public void registerSession() throws IOException {
        final TcpExceptionHandler throwing = TcpExceptionHandler.throwing();
        final TcpConnectionHandler tcpConnectionHandler = new TcpConnectionHandler(throwing);
        final FixEngine fixEngine = new FixEngine(tcpConnectionHandler, ExceptionHandler.logging());
        final TcpConnectionAcceptor tcpConnectionAcceptor = new TcpConnectionAcceptor("localhost", 0, throwing, tcpConnectionHandler);
        final MessageLog inbound = new SimpleMessageLog("");
        final MessageLog outbound = new SimpleMessageLog("");
        final FixSession acceptor = new FixSession(tcpConnectionAcceptor, inbound, outbound);
        fixEngine.register(acceptor);
        assertThat(fixEngine.fixSessions()).contains(acceptor);
    }

    @Test
    public void start() throws IOException {
        final TcpExceptionHandler tcpExceptionHandler = TcpExceptionHandler.throwing();
        final TcpConnectionHandler tcpConnectionHandler = new TcpConnectionHandler(tcpExceptionHandler);
        final FixEngine fixEngine = new FixEngine(tcpConnectionHandler, ExceptionHandler.logging());
        fixEngine.start();

        final TcpConnectionAcceptor tcpConnectionAcceptor = new TcpConnectionAcceptor("localhost", 0, tcpExceptionHandler, tcpConnectionHandler);
        final MessageLog acceptorInbound = new SimpleMessageLog("");
        final MessageLog acceptorOutbound = new SimpleMessageLog("");
        final FixSession acceptor = new FixSession(tcpConnectionAcceptor, acceptorInbound, acceptorOutbound);
        fixEngine.register(acceptor);

        while (boundPort(tcpConnectionAcceptor) <= 0);

        final TcpConnectionInitiator tcpConnectionInitiator = new TcpConnectionInitiator("localhost", tcpConnectionAcceptor.serverSocketChannel().socket().getLocalPort(), tcpExceptionHandler, tcpConnectionHandler);
        final MessageLog initiatorInbound = new SimpleMessageLog("");
        final MessageLog initiatorOutbound = new SimpleMessageLog("");
        final FixSession initiator = new FixSession(tcpConnectionInitiator, initiatorInbound, initiatorOutbound);
        fixEngine.register(initiator);

        fixEngine.terminate(120, TimeUnit.SECONDS);
    }

    private int boundPort(TcpConnectionAcceptor acceptor) {
        if (acceptor.serverSocketChannel() == null) {
            return -1;
        }

        return acceptor.serverSocketChannel().socket().getLocalPort();
    }

}
