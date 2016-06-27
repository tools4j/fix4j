package au.ryanlea.waddle.supreme.net;

import au.ryanlea.waddle.supreme.ExceptionHandler;
import au.ryanlea.waddle.supreme.log.MessageLog;
import au.ryanlea.waddle.supreme.OffHeapBuffer;
import au.ryanlea.waddle.supreme.UnsafeBuffer;

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
