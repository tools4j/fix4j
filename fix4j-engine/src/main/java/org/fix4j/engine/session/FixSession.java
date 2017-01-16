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
import org.fix4j.engine.Message;
import org.fix4j.engine.MessageFactory;
import org.fix4j.engine.net.SocketConnection;
import org.fix4j.engine.type.AsciiString;
import org.tools4j.mmap.io.MessageReader;
import org.tools4j.mmap.io.MessageWriter;
import org.tools4j.mmap.queue.Appender;
import org.tools4j.mmap.queue.Enumerator;
import org.tools4j.mmap.queue.MappedQueue;

import java.io.IOException;
import java.time.Clock;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by ryan on 2/06/16.
 */
public class FixSession {

    public enum MessageType {
        HEARTBEAT,
        TEST_REQUEST,
        RESEND_REQUEST,
        REJECT,
        SEQUENCE_RESET,
        LOGOUT,
        LOGON,
    }

    private final SocketConnection socketConnection;

    private final SessionLifecycle sessionLifecycle;

    private final MessageFactory messageFactory;

    private final Application application;

    private final MappedQueue inbound;

    private final Appender inboundAppender;

    private final Enumerator inboundEnumerator;

    private final MappedQueue outbound;

    private final Enumerator outboundEnumerator;

    private final Appender outboundAppender;

    private final AtomicInteger sequenceNumber = new AtomicInteger(0);

    private final ReadAsAsciiString readAsAsciiString = new ReadAsAsciiString();

    public FixSession(final SocketConnection socketConnection,
                      final SessionLifecycle sessionLifecycle,
                      final MessageFactory messageFactory,
                      final Application application,
                      final MappedQueue inbound,
                      final MappedQueue outbound) {
        this.socketConnection = socketConnection;
        this.sessionLifecycle = sessionLifecycle;
        this.messageFactory = messageFactory;
        this.application = application;
        this.inbound = inbound;
        this.inboundAppender = inbound.appender();
        this.inboundEnumerator = inbound.enumerator();
        this.outbound = outbound;
        this.outboundAppender = outbound.appender();
        this.outboundEnumerator = outbound.enumerator();
    }

    public FixSession send(final Message.Encodable message) {
        final int nextSequenceNumber = this.sequenceNumber.incrementAndGet();
        final Clock systemUTC = Clock.systemUTC();
        final MessageWriter messageWriter = outboundAppender.appendMessage();
        // write the sequence number and provide space for the message length as well
        message.encode(nextSequenceNumber, systemUTC, messageWriter);
        messageWriter.finishWriteMessage();
        return this;
    }

    public FixSession fromWire() {
        socketConnection.readInto(inboundAppender);
        return this;
    }

    public FixSession toWire() {
        socketConnection.writeFrom(outboundEnumerator);
        return this;
    }

    public FixSession process() {
        final SessionLifecycle.Bound bound = sessionLifecycle.bind(this);

        // read messages from inbound
        if (inboundEnumerator.hasNextMessage()) {
            final MessageReader messageReader = inboundEnumerator.readNextMessage();
            messageReader.getStringAscii(readAsAsciiString.reset());
            final Message.Decodable message = messageFactory.create(readAsAsciiString.asciiString);

            bound.onMessage(message);
            application.onMessage(message, this);
            messageReader.finishReadMessage();
        }

        // add heartbeat or test request if required.
        bound.manage();
        return this;
    }

    private final class ReadAsAsciiString implements Appendable {

        private final AsciiString.Mutable asciiString = new AsciiString.Mutable(2048);

        @Override
        public Appendable append(final CharSequence csq) throws IOException {
            return append(csq, 0, csq.length());
        }

        @Override
        public Appendable append(final CharSequence csq, final int start, final int end) throws IOException {
            for (int i = start; i < end; i++) {
                asciiString.append(csq.charAt(i));
            }
            return this;
        }

        @Override
        public Appendable append(char c) throws IOException {
            asciiString.append(c);
            return this;
        }

        public Appendable reset() {
            asciiString.reset();
            return this;
        }
    }

}
