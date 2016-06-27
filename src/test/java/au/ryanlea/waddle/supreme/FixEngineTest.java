/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 fix4j.org (tools4j.org)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package au.ryanlea.waddle.supreme;

import au.ryanlea.waddle.supreme.log.LogEntry;
import au.ryanlea.waddle.supreme.log.MessageLog;
import au.ryanlea.waddle.supreme.log.UnsafeMessageLog;
import au.ryanlea.waddle.supreme.net.TcpConnectionAcceptor;
import au.ryanlea.waddle.supreme.net.TcpConnectionHandler;
import au.ryanlea.waddle.supreme.net.TcpConnectionInitiator;
import au.ryanlea.waddle.supreme.net.TcpExceptionHandler;
import au.ryanlea.waddle.supreme.session.FixSessionAcceptor;
import au.ryanlea.waddle.supreme.session.FixSessionInitiator;
import au.ryanlea.waddle.supreme.spec.Fix4SessionLifecycle;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@Ignore
public class FixEngineTest {

    private static final int TWO_MB = 2 * 1024 * 1024;
    private static final long MAX_WAIT_MILLIS = 5000;

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void start() throws IOException {
        final TcpExceptionHandler tcpExceptionHandler = TcpExceptionHandler.throwing();
        final TcpConnectionHandler tcpConnectionHandler = new TcpConnectionHandler(tcpExceptionHandler);
        final FixEngine fixEngine = new FixEngine(tcpConnectionHandler, ExceptionHandler.logging());
        fixEngine.start();

        final TcpConnectionAcceptor tcpConnectionAcceptor = new TcpConnectionAcceptor("localhost", 0, tcpExceptionHandler, tcpConnectionHandler);
        final MessageLog acceptorInbound = new UnsafeMessageLog(temporaryFolder.newFile("acceptor-inbound.log").getAbsolutePath(), TWO_MB, ExceptionHandler.throwing());
        final MessageLog acceptorOutbound = new UnsafeMessageLog(temporaryFolder.newFile("acceptor-outbound.log").getAbsolutePath(), TWO_MB, ExceptionHandler.throwing());
        final FixSessionAcceptor acceptor = new FixSessionAcceptor(
                tcpConnectionAcceptor,
                acceptorInbound,
                acceptorOutbound,
                () -> new Fix4SessionLifecycle.Acceptor(),
                AcceptorApplication::new);
        fixEngine.register(acceptor);

        final long time = System.currentTimeMillis();
        while (boundPort(tcpConnectionAcceptor) <= 0) {
            Thread.yield();
            if (System.currentTimeMillis() - time > MAX_WAIT_MILLIS) {
                throw new RuntimeException("timeout, max wait time exceeded: " + MAX_WAIT_MILLIS + "ms");
            }
        }

        final TcpConnectionInitiator tcpConnectionInitiator = new TcpConnectionInitiator("localhost", tcpConnectionAcceptor.serverSocketChannel().socket().getLocalPort(), tcpExceptionHandler, tcpConnectionHandler);
        final MessageLog initiatorInbound = new UnsafeMessageLog(temporaryFolder.newFile("initiator-inbound.log").getAbsolutePath(), TWO_MB, ExceptionHandler.throwing());
        final MessageLog initiatorOutbound = new UnsafeMessageLog(temporaryFolder.newFile("initiator-outbound.log").getAbsolutePath(), TWO_MB, ExceptionHandler.throwing());
        final FixSessionInitiator initiator = new FixSessionInitiator(
                tcpConnectionInitiator,
                initiatorInbound,
                initiatorOutbound,
                () -> new Fix4SessionLifecycle.Initiator(sessionMessageType -> {
                    switch (sessionMessageType) {
                        case LOGON:
                            return new StringMessage("logon");
                    }
                    return null;
                }),
                InitiatingApplication::new);
        fixEngine.register(initiator);

        fixEngine.terminate(120, TimeUnit.SECONDS);
    }

    private int boundPort(TcpConnectionAcceptor acceptor) {
        if (acceptor.serverSocketChannel() == null) {
            return -1;
        }

        return acceptor.serverSocketChannel().socket().getLocalPort();
    }

    private static class InitiatingApplication implements Application, Consumer<LogEntry> {

        @Override
        public Consumer<LogEntry> consume() {
            return this;
        }

        @Override
        public void accept(LogEntry logEntry) {

        }
    }

    private static class AcceptorApplication implements Application, Consumer<LogEntry> {

        @Override
        public Consumer<LogEntry> consume() {
            return this;
        }

        @Override
        public void accept(LogEntry logEntry) {

        }
    }

}
