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
package au.ryanlea.waddle.supreme.session;

import au.ryanlea.waddle.supreme.FixEngine;
import au.ryanlea.waddle.supreme.MessageLog;
import au.ryanlea.waddle.supreme.net.SocketConnection;
import au.ryanlea.waddle.supreme.net.TcpConnectionInitiator;

import java.util.function.Consumer;

/**
 * Created by ryan on 3/06/16.
 */
public class FixSessionInitiator implements FixSessionConnection {

    private final TcpConnectionInitiator tcpConnection;

    private final FixEngine fixEngine;

    private final MessageLog inbound;

    private final MessageLog outbound;

    public FixSessionInitiator(final TcpConnectionInitiator tcpConnection,
                               final FixEngine fixEngine,
                               final MessageLog inbound,
                               final MessageLog outbound) {
        this.tcpConnection = tcpConnection;
        this.fixEngine = fixEngine;
        this.inbound = inbound;
        this.outbound = outbound;
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
        onFixSession.accept(new FixSession(socketConnection, inbound, outbound));
        return this;
    }

}
