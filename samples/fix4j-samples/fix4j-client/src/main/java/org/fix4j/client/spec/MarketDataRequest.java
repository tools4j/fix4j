package org.fix4j.client.spec;

import org.fix4j.engine.Message;
import org.fix4j.engine.codec.FixEncoder;
import org.fix4j.engine.type.AsciiString;
import org.tools4j.mmap.io.MessageWriter;

import java.time.Clock;

import static org.fix4j.engine.util.EncodeUtil.generateChecksum;

public class MarketDataRequest implements SpecMessage {

    private final Decoder decoder = new Decoder();
    private final Encoder encoder = new Encoder();
    private final Header header = new Header();
    private final Trailer trailer = new Trailer();

    private final AsciiString.Mutable mdReqId = new AsciiString.Mutable(128);
    private final AsciiString.Mutable symbol = new AsciiString.Mutable(128);
    private char subscriptionRequestType;

    public MarketDataRequest mdReqId(final AsciiString mdReqId) {
        this.mdReqId.reset();
        this.mdReqId.append(mdReqId);
        return this;
    }

    public MarketDataRequest symbol(final AsciiString symbol) {
        this.symbol.reset();
        this.symbol.append(symbol);
        return this;
    }

    public MarketDataRequest subscriptionRequestType(final char subscriptionRequestType) {
        this.subscriptionRequestType = subscriptionRequestType;
        return this;
    }

    public Decodable decodable(final AsciiString content) {
        return decoder;
    }

    @Override
    public Encodable encodable() {
        return encoder;
    }

    public Header header() {
        return header;
    }

    private final class Decoder implements Decodable {

        @Override
        public MsgType msgType() {
            return MsgType.MARKET_DATA_REQUEST;
        }
    }

    private final class Encoder implements Encodable, AsciiString {

        private final FixEncoder fixEncoder = new FixEncoder();
        private final AsciiString.Mutable content = new AsciiString.Mutable(128);

        @Override
        public void encode(int sequenceNumber, Clock clock, MessageWriter messageWriter) {
            fixEncoder.wrap(content)
                    .tag(262).value(mdReqId)
                    .tag(263).value(subscriptionRequestType)
                    .tag(264).value(1)
                    .tag(146).value(1)
                    .tag(55).value(symbol);

            header
                    .msgType("V")
                    .msgSeqNum(sequenceNumber)
                    .sendingTime(clock.millis())
                    .encode();

            trailer
                    .encode();
            header
                    .bodyLength(header.content().length() + content.length() + trailer.length());

            trailer
                    .checksum(generateChecksum(header, content, trailer));

            messageWriter.putStringAscii(this);
        }

        @Override
        public int length() {
            return header.length() + content.length() + trailer.length();
        }

        @Override
        public byte byteAt(int idx) {
            if (idx < header.length()) {
                return header.byteAt(idx);
            } else if (idx < header.length() + content.length()) {
                return (byte) content.charAt(idx - header.length());
            } else if (idx < length()) {
                return trailer.byteAt(idx - header.length() - content.length());
            }
            throw new IndexOutOfBoundsException("index[" + idx + "], length [" + length() + "]");
        }

    }
}
