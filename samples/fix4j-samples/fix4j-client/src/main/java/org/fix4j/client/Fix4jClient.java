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
package org.fix4j.client;

import org.fix4j.client.spec.Fix4jClientMessageFactory;
import org.fix4j.client.spec.LogonMessage;
import org.fix4j.engine.Application;
import org.fix4j.engine.ExceptionHandler;
import org.fix4j.engine.FixEngine;
import org.fix4j.engine.net.TcpConnectionHandler;
import org.fix4j.engine.net.TcpConnectionInitiator;
import org.fix4j.engine.net.TcpExceptionHandler;
import org.fix4j.engine.session.FixSessionConfiguration;
import org.fix4j.engine.session.FixSessionInitiator;
import org.fix4j.engine.spec.Fix4SessionLifecycle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tools4j.mmap.queue.MappedQueue;
import org.tools4j.mmap.queue.OneToManyQueue;

import java.io.IOException;

/**
 * Created by ryan on 4/12/16.
 */
public class Fix4jClient {

    public static void main(final String[] args) throws IOException {
        new Fix4jClient().run();
    }

    private void run() throws IOException {
        final FixSessionConfiguration fixSessionConfiguration = new FixSessionConfiguration()
                .senderCompId("FIX4J-INITIATOR")
                .targetCompId("QUICKFIXJ-ACCEPTOR");
        final TcpConnectionHandler tcpConnectionHandler = new TcpConnectionHandler(TcpExceptionHandler.throwing());
        final FixEngine fixEngine = new FixEngine(tcpConnectionHandler, ExceptionHandler.logging());
        fixEngine.start();

        final int TWO_MB = 2 * 1024 * 1024;
        final TcpConnectionInitiator tcpConnectionInitiator = new TcpConnectionInitiator("localhost", 12000, TcpExceptionHandler.throwing(), tcpConnectionHandler);
        final MappedQueue inbound = OneToManyQueue.createOrAppend("./logs/inbound.log");
        final MappedQueue outbound = OneToManyQueue.createOrAppend("./logs/outbound.log");
        final FixSessionInitiator initiator = new FixSessionInitiator(
                tcpConnectionInitiator,
                inbound,
                outbound,
                () -> new Fix4SessionLifecycle.Initiator(sessionMessageType -> {
                    switch (sessionMessageType) {
                        case LOGON:
                            final LogonMessage logonMessage = new LogonMessage();
                            logonMessage.header()
                                    .senderCompId(fixSessionConfiguration.senderCompId())
                                    .targetCompId(fixSessionConfiguration.targetCompId());
                            return logonMessage;
                    }
                    return null;
                }),
                Fix4jClientMessageFactory::new,
                Fix4jClientApplication::new
                );
        fixEngine.register(initiator);

        while (!Thread.interrupted()) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private static final class Fix4jClientApplication implements Application {

        private static final Logger log = LoggerFactory.getLogger(Fix4jClientApplication.class);


    }

}
