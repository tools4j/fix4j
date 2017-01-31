package org.fix4j.client.spec;

import org.fix4j.engine.codec.FixDecoder;
import org.fix4j.engine.codec.FixEncoder;
import org.fix4j.engine.codec.ValueDecoder;
import org.fix4j.engine.type.AsciiString;
import org.tools4j.mmap.io.MessageWriter;

import java.time.Clock;

/**
 * Created by ryan on 16/01/17.
 */
public class Reject implements SpecMessage {

    private final Encoder encoder = new Encoder();
    private final Decoder decoder = new Decoder();

    @Override
    public Inbound asInbound(final AsciiString content) {
        return decoder.wrap(content);
    }

    @Override
    public Outbound asOutbound() {
        return encoder;
    }

    public final class Encoder implements Outbound, AsciiString {

        private final FixEncoder fixEncoder = new FixEncoder();
        private final AsciiString.Mutable content = new AsciiString.Mutable(128);

        @Override
        public byte byteAt(int index) {
            return 0;
        }

        @Override
        public int length() {
            return 0;
        }

        @Override
        public void encode(int sequenceNumber, Clock clock, MessageWriter messageWriter) {

        }
    }

    public final class Decoder implements Inbound {

        private final ValueHandler valueHandler = new ValueHandler();

        private final FixDecoder fixDecoder = new FixDecoder();

        private final AsciiString.Mutable refMsgType = new AsciiString.Mutable(128);

        private int refTagId;

        private final AsciiString.Mutable text = new AsciiString.Mutable(128);

        private Decoder wrap(final AsciiString content) {
            this.refMsgType.reset();
            this.refTagId = 0;
            this.text.reset();
            this.fixDecoder.wrap(content);
            return this;
        }

        @Override
        public MsgType msgType() {
            return MsgType.REJECT;
        }

        public AsciiString refMsgType() {
            while (this.refMsgType.length() == 0 && this.fixDecoder.hasNext()) {
                this.fixDecoder.tag(valueHandler);
            }
            return refMsgType;
        }

        public int refTagId() {
            while (this.refTagId == 0 && this.fixDecoder.hasNext()) {
                this.fixDecoder.tag(valueHandler);
            }
            return refTagId;
        }

        public AsciiString text() {
            while (this.text.length() == 0 && this.fixDecoder.hasNext()) {
                this.fixDecoder.tag(valueHandler);
            }
            return text;
        }

        private final class ValueHandler implements org.fix4j.engine.codec.ValueHandler {

            @Override
            public void handle(int tag, ValueDecoder valueDecoder) {
                switch (tag) {
                    case 371:
                        refTagId = valueDecoder.getInt();
                        break;
                    case 372:
                        valueDecoder.getString(refMsgType);
                        break;
                    case 58:
                        valueDecoder.getString(text);
                        break;
                }
            }
        }

    }
}
