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

import org.fix4j.engine.Buffer;
import org.fix4j.engine.codec.FixEncoder;
import org.fix4j.engine.type.AsciiString;
import org.fix4j.engine.type.UTCTimestamp;

public class Header implements Buffer {

    private final AsciiString firstTwoFields = new AsciiString(16);

    private final AsciiString content = new AsciiString(128);

    private final FixEncoder fixEncoder = new FixEncoder();

    private final AsciiString beginString = new AsciiString("FIX.4.2");

    private AsciiString msgType = new AsciiString(8);

    private AsciiString senderCompId = new AsciiString(32);

    private AsciiString targetCompId = new AsciiString(32);

    private int msgSeqNum;

    private UTCTimestamp sendingTime = new UTCTimestamp();

    public Header bodyLength(final int bodyLength) {
        fixEncoder.wrap(firstTwoFields)
                .tag(9).value(bodyLength);
        return this;
    }

    public Header msgType(final CharSequence msgType) {
        this.msgType.append(msgType);
        return this;
    }

    public Header senderCompId(final CharSequence senderCompId) {
        this.senderCompId.append(senderCompId);
        return this;
    }

    public Header targetCompId(final CharSequence targetCompId) {
        this.targetCompId.append(targetCompId);
        return this;
    }

    public Header msgSeqNum(final int msgSeqNum) {
        this.msgSeqNum = msgSeqNum;
        return this;
    }

    public Header sendingTime(final UTCTimestamp sendingTime) {
        this.sendingTime.epochMillis(sendingTime.epochMillis());
        return this;
    }

    public Header sendingTime(final long sendingTime) {
        this.sendingTime.epochMillis(sendingTime);
        return this;
    }

    @Override
    public int length() {
        return firstTwoFields.length() + content.length();
    }

    @Override
    public byte getByte(int idx) {
        if (idx < firstTwoFields.length()) {
            return (byte) firstTwoFields.charAt(idx);
        } else if (idx < length()) {
            return (byte) content.charAt(idx - firstTwoFields.length());
        }
        throw new IndexOutOfBoundsException("index[" + idx + "], length [" + length() + "]");
    }

    @Override
    public Buffer putByte(byte b) {
        content.putByte(b);
        return this;
    }

    public void reset() {
        content.reset();
    }

    public void encode() {
        fixEncoder.wrap(firstTwoFields)
                .tag(8).value(beginString);

        fixEncoder.wrap(content)
                .tag(35).value(msgType)
                .tag(49).value(senderCompId)
                .tag(56).value(targetCompId)
                .tag(34).value(msgSeqNum)
                .tag(52).value(sendingTime);
    }

    public CharSequence content() {
        return content;
    }
}
