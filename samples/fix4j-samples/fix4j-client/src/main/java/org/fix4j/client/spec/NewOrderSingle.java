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
package org.fix4j.client.spec;

import org.fix4j.engine.codec.FixDecoder;
import org.fix4j.engine.codec.FixEncoder;
import org.fix4j.engine.type.AsciiString;
import org.fix4j.engine.type.Qty;
import org.fix4j.engine.type.UTCTimestamp;
import org.tools4j.mmap.io.MessageWriter;

import java.time.Clock;

import static org.fix4j.engine.util.EncodeUtil.generateChecksum;

public class NewOrderSingle implements SpecMessage {

    private final InboundReader inboundReader = new InboundReader();
    private final OutboundWriter outboundWriter = new OutboundWriter();
    private final Header header = new Header();
    private final Trailer trailer = new Trailer();

    private final AsciiString.Mutable clOrdId = new AsciiString.Mutable(128);
    private final AsciiString.Mutable symbol = new AsciiString.Mutable(128);
    private char side;
    private final UTCTimestamp transactTime = new UTCTimestamp();
    private final Qty ordQty = new Qty();
    private char ordType;

    public NewOrderSingle clOrdId(final AsciiString clOrdId) {
        this.clOrdId.reset();
        this.clOrdId.append(clOrdId);
        return this;
    }

    public NewOrderSingle symbol(final AsciiString symbol) {
        this.symbol.reset();
        this.symbol.append(symbol);
        return this;
    }

    public NewOrderSingle side(final char side) {
        this.side = side;
        return this;
    }

    public NewOrderSingle transactTime(final UTCTimestamp transactTime) {
        this.transactTime.epochMillis(transactTime.epochMillis());
        return this;
    }

    public NewOrderSingle ordQty(final Qty ordQty) {
        this.ordQty.value(ordQty.value());
        return this;
    }

    public NewOrderSingle ordType(final char ordType) {
        this.ordType = ordType;
        return this;
    }

    @Override
    public Inbound asInbound(final AsciiString content) {
        return inboundReader.wrap(content);
    }

    @Override
    public Outbound asOutbound() {
        return outboundWriter;
    }

    public Header header() {
        return header;
    }

    private final class InboundReader implements Inbound {

        private final FixDecoder fixDecoder = new FixDecoder();

        @Override
        public MsgType msgType() {
            return MsgType.NEW_ORDER_SINGLE;
        }

        public InboundReader wrap(final AsciiString content) {
            fixDecoder.wrap(content);
            return this;
        }
    }

    public final class OutboundWriter implements Outbound, AsciiString {

        private final FixEncoder fixEncoder = new FixEncoder();
        private final AsciiString.Mutable content = new AsciiString.Mutable(128);

        public void encode(int sequenceNumber, Clock clock, MessageWriter messageWriter) {
            fixEncoder.wrap(content)
                    .tag(11).value(clOrdId)
                    .tag(55).value(symbol)
                    .tag(54).value(side)
                    .tag(60).value(transactTime)
                    .tag(38).value((long) ordQty.value())
                    .tag(40).value(ordType);

            header
                    .msgType("D")
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
