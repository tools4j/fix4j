package org.fix4j.client.spec;

import org.fix4j.engine.codec.FixEncoder;
import org.fix4j.engine.type.AsciiString;
import org.tools4j.mmap.io.MessageWriter;

import java.time.Clock;

import static org.fix4j.engine.util.EncodeUtil.generateChecksum;

/**
 * Created by ryan on 10/01/17.
 */
public class Heartbeat implements SpecMessage {

    private final Encoder encoder = new Encoder();
    private final Decoder decoder = new Decoder();

    private final Header header = new Header();
    private final Trailer trailer = new Trailer();

    private AsciiString.Mutable testReqId = new AsciiString.Mutable(128);

    public Inbound asInbound(final AsciiString content) {
        return decoder.wrap(content);
    }

    @Override
    public Outbound asOutbound() {
        return encoder;
    }

    public Heartbeat testReqId(final AsciiString testReqId) {
        this.testReqId.reset();
        this.testReqId.append(testReqId);
        return this;
    }

    public Header header() {
        return header;
    }

    public final class Encoder implements Outbound, AsciiString {

        private final FixEncoder fixEncoder = new FixEncoder();
        private final AsciiString.Mutable content = new AsciiString.Mutable(128);

        @Override
        public void encode(final int sequenceNumber, final Clock clock, final MessageWriter messageWriter) {
            fixEncoder.wrap(content)
                    .tag(112).value(testReqId);

            header
                    .msgType("0")
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

    public final class Decoder implements Inbound {

        private AsciiString content;

        public Inbound wrap(final AsciiString content) {
            this.content = content;
            return this;
        }

        @Override
        public MsgType msgType() {
            return MsgType.HEARTBEAT;
        }
    }

}
