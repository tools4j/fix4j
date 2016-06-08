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

/**
 * Created by ryan on 2/06/16.
 */
public class FixSession {

    private final TcpConnection tcpConnection;

    private final MessageLog inbound;

    private final MessageLog outbound;

    private boolean loggedOn;

    public FixSession(final TcpConnection tcpConnection, final MessageLog inbound, final MessageLog outbound) {
        this.tcpConnection = tcpConnection;
        this.inbound = inbound;
        this.outbound = outbound;
    }

    public FixSession establish() {
        tcpConnection.establish(this);
        return this;
    }

    public FixSession send(Message message) {
        outbound.readFrom(message);
        return this;
    }

    public FixSession fromWire() {
        tcpConnection.readInto(inbound);
        return this;
    }

    public TcpConnection tcpConnection() {
        return tcpConnection;
    }

    public FixSession toWire() {
        tcpConnection.writeFrom(outbound);
        return this;
    }

    public FixSession process() {
        // readFrom messages from inbound

        // add heartbeat or test request if required.
        logon();

        return this;
    }

    private void logon() {
        if (tcpConnection.isConnected() && !loggedOn) {
            send(new StringMessage("logon"));
        }
    }

}
