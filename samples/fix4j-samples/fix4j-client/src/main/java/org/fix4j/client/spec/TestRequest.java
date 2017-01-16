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

import org.fix4j.engine.Message;
import org.fix4j.engine.codec.FixDecoder;
import org.fix4j.engine.codec.TagDecoder;
import org.fix4j.engine.codec.ValueHandler;
import org.fix4j.engine.type.AsciiString;
import org.tools4j.mmap.io.MessageWriter;

import java.time.Clock;

/**
 * Created by ryan on 14/12/16.
 */
public class TestRequest implements SpecMessage {

    private final Decoder decoder = new Decoder();
    private final Encoder encoder = new Encoder();

    public final class Decoder implements Decodable {

        private final FixDecoder fixDecoder = new FixDecoder();
        private TagDecoder tagDecoder;

        private final AsciiString.Mutable testReqId = new AsciiString.Mutable(128);
        private ValueHandler decoding = (tag, valueDecoder) -> {
            switch (tag) {
                case 112:
                    valueDecoder.getString(testReqId);
                    break;
            }
        };

        private Decoder wrap(final AsciiString content) {
            testReqId.reset();
            tagDecoder = fixDecoder.wrap(content);
            return this;
        }

        @Override
        public MsgType msgType() {
            return MsgType.TEST_REQUEST;
        }

        public AsciiString testReqId() {
            while (testReqId.length() == 0 && tagDecoder.hasNext()) {
                fixDecoder.tag(decoding);
            }
            return testReqId;
        }
    }

    public final class Encoder implements Encodable {

        @Override
        public void encode(int sequenceNumber, Clock clock, MessageWriter messageWriter) {

        }
    }

    @Override
    public Decodable decodable(final AsciiString content) {
        return decoder.wrap(content);
    }

    @Override
    public Encodable encodable() {
        return encoder;
    }
}
