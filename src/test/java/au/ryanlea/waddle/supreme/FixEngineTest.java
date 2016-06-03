package au.ryanlea.waddle.supreme;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 * Created by ryan on 1/06/16.
 */
public class FixEngineTest {

    public static final int TWO_MB = 2 * 1024 * 1024;
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void registerSession() throws IOException {
        final TcpExceptionHandler throwing = TcpExceptionHandler.throwing();
        final TcpConnectionHandler tcpConnectionHandler = new TcpConnectionHandler(throwing);
        final FixEngine fixEngine = new FixEngine(tcpConnectionHandler, ExceptionHandler.logging());
        final TcpConnectionAcceptor tcpConnectionAcceptor = new TcpConnectionAcceptor("localhost", 0, throwing, tcpConnectionHandler);
        final MessageLog inbound = mock(MessageLog.class);
        final MessageLog outbound = mock(MessageLog.class);
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
        final MessageLog acceptorInbound = new UnsafeMessageLog(temporaryFolder.newFile("acceptor-inbound.log").getAbsolutePath(), TWO_MB);
        final MessageLog acceptorOutbound = new UnsafeMessageLog(temporaryFolder.newFile("acceptor-outbound.log").getAbsolutePath(), TWO_MB);
        final FixSession acceptor = new FixSession(tcpConnectionAcceptor, acceptorInbound, acceptorOutbound);
        fixEngine.register(acceptor);

        while (boundPort(tcpConnectionAcceptor) <= 0);

        final TcpConnectionInitiator tcpConnectionInitiator = new TcpConnectionInitiator("localhost", tcpConnectionAcceptor.serverSocketChannel().socket().getLocalPort(), tcpExceptionHandler, tcpConnectionHandler);
        final MessageLog initiatorInbound = new UnsafeMessageLog(temporaryFolder.newFile("initiator-inbound.log").getAbsolutePath(), TWO_MB);
        final MessageLog initiatorOutbound = new UnsafeMessageLog(temporaryFolder.newFile("initiator-outbound.log").getAbsolutePath(), TWO_MB);
        final FixSession initiator = new FixSessionInitiator(tcpConnectionInitiator, initiatorInbound, initiatorOutbound);
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
