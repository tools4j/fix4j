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
package org.fix4j.engine.net;

import org.fix4j.engine.ExceptionHandler;
import org.fix4j.engine.OffHeapBuffer;
import org.fix4j.engine.UnsafeBuffer;
import org.fix4j.engine.log.MessageLog;

import java.nio.channels.SocketChannel;

/**
 * Created by ryan on 22/06/16.
 */
public interface SocketConnection {

    SocketConnection readInto(MessageLog messageLog);

    SocketConnection writeFrom(MessageLog messageLog);

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

        public SocketConnection readInto(MessageLog messageLog) {
            inbound.readFrom(socketChannel);
            if (inbound.bytesToRead() > 0) {
                messageLog.readFrom(inbound);
            }
            return this;
        }

        public SocketConnection writeFrom(MessageLog messageLog) {
            messageLog.writeTo(outbound);
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
        public SocketConnection readInto(MessageLog messageLog) {
            return this;
        }

        @Override
        public SocketConnection writeFrom(MessageLog messageLog) {
            return this;
        }

        @Override
        public boolean isConnected() {
            return false;
        }
    }

}
