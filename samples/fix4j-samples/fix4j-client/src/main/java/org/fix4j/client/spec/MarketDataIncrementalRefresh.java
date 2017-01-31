package org.fix4j.client.spec;

import org.fix4j.engine.codec.FixDecoder;
import org.fix4j.engine.type.AsciiString;
import org.tools4j.mmap.io.MessageWriter;

import java.time.Clock;

/**
 * Created by ryan on 17/01/17.
 */
public class MarketDataIncrementalRefresh implements SpecMessage {

    private final Encoder encoder = new Encoder();

    private final Decoder decoder = new Decoder();

    @Override
    public Inbound asInbound(AsciiString content) {
        return decoder.wrap(content);
    }

    @Override
    public Outbound asOutbound() {
        return encoder;
    }

    public final class Decoder implements Inbound {

        private final FixDecoder fixDecoder = new FixDecoder();

        public Decoder wrap(final AsciiString content) {
            this.fixDecoder.wrap(content);
            return this;
        }

        @Override
        public MsgType msgType() {
            return MsgType.MARKET_DATA_SNAPSHOT_INCREMENTAL_REFRESH;
        }

        public AsciiString symbol() {
            return null;
        }
    }

    private final class Encoder implements Outbound {

        @Override
        public void encode(int sequenceNumber, Clock clock, MessageWriter messageWriter) {

        }
    }
}
