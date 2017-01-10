/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-2017 fix4j.org (tools4j.org)
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
package org.fix4j.engine.session;

import org.fix4j.engine.Application;
import org.fix4j.engine.net.SocketConnection;
import org.fix4j.engine.net.TcpConnectionAcceptor;
import org.tools4j.mmap.queue.MappedQueue;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Created by ryan on 22/06/16.
 */
public class FixSessionAcceptor implements FixSessionConnection {

    private final TcpConnectionAcceptor tcpConnection;

    private final MappedQueue inbound;

    private final MappedQueue outbound;

    private final Supplier<SessionLifecycle> sessionLifecycleSupplier;

    private final Supplier<Application> applicationSupplier;

    public FixSessionAcceptor(final TcpConnectionAcceptor tcpConnection,
                              final MappedQueue inbound,
                              final MappedQueue outbound,
                              final Supplier<SessionLifecycle> sessionLifecycleSupplier,
                              final Supplier<Application> applicationSupplier) {
        this.tcpConnection = tcpConnection;
        this.inbound = inbound;
        this.outbound = outbound;
        this.sessionLifecycleSupplier = sessionLifecycleSupplier;
        this.applicationSupplier = applicationSupplier;
    }

    @Override
    public FixSessionConnection establish() {
        tcpConnection.establish(this);
        return this;
    }

    @Override
    public FixSessionConnection connect(Consumer<FixSession> onFixSession) {
        final SocketConnection socketConnection = tcpConnection.connect();
        // todo - inbound and outbound need to be constructed based upon something - or anything
        onFixSession.accept(new FixSession(
                socketConnection,
                sessionLifecycleSupplier.get(),
                message -> null,
                applicationSupplier.get(),
                inbound,
                outbound));
        return this;
    }

}
