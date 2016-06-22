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

import au.ryanlea.waddle.supreme.Message;
import au.ryanlea.waddle.supreme.MessageLog;
import au.ryanlea.waddle.supreme.StringMessage;
import au.ryanlea.waddle.supreme.net.SocketConnection;
import au.ryanlea.waddle.supreme.net.TcpConnection;

/**
 * Created by ryan on 2/06/16.
 */
public class FixSession {

    private final SocketConnection socketConnection;

    private final SessionLifecycle sessionLifecycle;

    private final MessageLog inbound;

    private final MessageLog outbound;

    public FixSession(final SocketConnection socketConnection,
                      final SessionLifecycle sessionLifecycle,
                      final MessageLog inbound,
                      final MessageLog outbound) {
        this.socketConnection = socketConnection;
        this.sessionLifecycle = sessionLifecycle;
        this.inbound = inbound;
        this.outbound = outbound;
    }

    public FixSession send(Message message) {
        outbound.readFrom(message);
        return this;
    }

    public FixSession fromWire() {
        socketConnection.readInto(inbound);
        return this;
    }

    public FixSession toWire() {
        socketConnection.writeFrom(outbound);
        return this;
    }

    public FixSession process() {
        // read messages from inbound

        // add heartbeat or test request if required.
        sessionLifecycle.manage(this);

        // write messages to outbound
        return this;
    }

}
