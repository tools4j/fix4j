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
package org.fix4j.client.spec;

import org.fix4j.engine.Message;
import org.fix4j.engine.codec.FixEncoder;
import org.fix4j.engine.type.AsciiString;
import org.tools4j.mmap.io.MessageWriter;

import java.time.Clock;

import static org.fix4j.engine.util.EncodeUtil.generateChecksum;

public class LogonMessage implements SpecMessage {

    private final Encoder encoder = new Encoder();
    private final Decoder decoder = new Decoder();

    private final Header header = new Header();
    private final Trailer trailer = new Trailer();

    private int encryptMethod;

    private int heartBtInt;

    private boolean resetSeqNumFlag;

    public Message.Decodable decodable(final AsciiString content) {
        return decoder.wrap(content);
    }

    public Header header() {
        return header;
    }

    public Trailer trailer() {
        return trailer;
    }

    public LogonMessage encryptMethod(final int encryptMethod) {
        this.encryptMethod = encryptMethod;
        return this;
    }

    public LogonMessage heartBtInt(final int heartBtInt) {
        this.heartBtInt = heartBtInt;
        return this;
    }

    public LogonMessage resetSeqNumFlag(final boolean resetSeqNumFlag) {
        this.resetSeqNumFlag = resetSeqNumFlag;
        return this;
    }

    public Encodable encodable() {
        return encoder;
    }

    public final class Encoder implements Message.Encodable, AsciiString {

        private final FixEncoder fixEncoder = new FixEncoder();
        private final AsciiString.Mutable content = new AsciiString.Mutable(128);

        @Override
        public void encode(final int sequenceNumber, final Clock clock, final MessageWriter messageWriter) {
            fixEncoder.wrap(content)
                    .tag(98).value(encryptMethod)
                    .tag(108).value(heartBtInt)
                    .tag(141).value(resetSeqNumFlag);

            header
                    .msgType("A")
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

    public final class Decoder implements Message.Decodable {

        private AsciiString content;

        public Decodable wrap(final AsciiString content) {
            this.content = content;
            return this;
        }

        @Override
        public MsgType msgType() {
            return MsgType.LOGON;
        }
    }

}
