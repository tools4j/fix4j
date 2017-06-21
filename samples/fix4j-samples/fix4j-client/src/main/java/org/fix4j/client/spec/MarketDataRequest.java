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

import org.fix4j.engine.codec.FixEncoder;
import org.fix4j.engine.type.AsciiString;
import org.tools4j.mmap.io.MessageWriter;

import java.time.Clock;
import java.util.ArrayList;
import java.util.List;

import static org.fix4j.engine.util.EncodeUtil.generateChecksum;

public class MarketDataRequest implements SpecMessage {

    private final Decoder decoder = new Decoder();
    private final Encoder encoder = new Encoder();
    private final Header header = new Header();
    private final Trailer trailer = new Trailer();

    private final AsciiString.Mutable mdReqId = new AsciiString.Mutable(128);
    private final AsciiString.Mutable symbol = new AsciiString.Mutable(128);
    private char subscriptionRequestType;

    // todo - need to create lists for primitives
    private final List<Character> mdEntryTypes = new ArrayList<>();

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

    public MarketDataRequest mdEntryType(final char mdEntryType) {
        this.mdEntryTypes.add(mdEntryType);
        return this;
    }

    public Inbound asInbound(final AsciiString content) {
        return decoder;
    }

    @Override
    public Outbound asOutbound() {
        return encoder;
    }

    public Header header() {
        return header;
    }

    private final class Decoder implements Inbound {

        @Override
        public MsgType msgType() {
            return MsgType.MARKET_DATA_REQUEST;
        }
    }

    private final class Encoder implements Outbound, AsciiString {

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

            fixEncoder.tag(267).value(mdEntryTypes.size());
            for (final char mdEntryType : mdEntryTypes) {
                fixEncoder.tag(269).value(mdEntryType);
            }

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
