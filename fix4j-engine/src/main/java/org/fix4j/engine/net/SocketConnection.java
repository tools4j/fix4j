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
package org.fix4j.engine.net;

import org.fix4j.engine.ExceptionHandler;
import org.fix4j.engine.io.OffHeapBuffer;
import org.fix4j.engine.io.UnsafeBuffer;
import org.tools4j.mmap.queue.Appender;
import org.tools4j.mmap.queue.Enumerator;

import java.nio.channels.SocketChannel;

/**
 * Created by ryan on 22/06/16.
 */
public interface SocketConnection {

    SocketConnection readInto(Appender messageLog);

    SocketConnection writeFrom(Enumerator messageLog);

    boolean isConnected();

    class Connected implements SocketConnection {

        private final SocketChannel socketChannel;

        private final OffHeapBuffer inbound;

        private final OffHeapBuffer outbound;

        public Connected(final SocketChannel socketChannel,
                                final ExceptionHandler exceptionHandler) {
            this.socketChannel = socketChannel;
            this.inbound = new UnsafeBuffer(UnsafeBuffer.TEN_MB, exceptionHandler);
            this.outbound = new UnsafeBuffer(UnsafeBuffer.TEN_MB, exceptionHandler);
        }

        public SocketConnection readInto(final Appender appender) {
            inbound.readFrom(socketChannel);
            if (inbound.bytesToRead() > 0) {
                // message splitting needs to happen here
                inbound.writeTo(appender.appendMessage());
            }
            return this;
        }

        public SocketConnection writeFrom(final Enumerator enumerator) {
            while (enumerator.hasNextMessage()) {
                outbound.readFrom(enumerator.readNextMessage());
            }
            if (outbound.bytesToRead() > 0) {
                outbound.writeTo(socketChannel);
            }
            return this;
        }

        @Override
        public boolean isConnected() {
            return true;
        }
    }

    class NotConnected implements SocketConnection {

        @Override
        public SocketConnection readInto(Appender messageLog) {
            return this;
        }

        @Override
        public SocketConnection writeFrom(Enumerator messageLog) {
            return this;
        }

        @Override
        public boolean isConnected() {
            return false;
        }
    }

}
