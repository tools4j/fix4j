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
import org.fix4j.engine.MessageFactory;
import org.fix4j.engine.codec.FixDecoder;
import org.fix4j.engine.codec.TagDecoder;
import org.fix4j.engine.codec.ValueDecoder;
import org.fix4j.engine.codec.ValueHandler;
import org.fix4j.engine.type.AsciiString;

import java.io.IOException;
import java.util.EnumMap;

public class Fix4jClientMessageFactory implements MessageFactory {

    private final FixDecoder fixDecoder = new FixDecoder();

    private final HeaderHandler headerHandler = new HeaderHandler();

    private final EnumMap<MsgType, SpecMessage> cache = new EnumMap<>(MsgType.class);

    public Fix4jClientMessageFactory() {
        for (final MsgType msgType : MsgType.values()) {
            cache.put(msgType, msgType.message());
        }
    }

    @Override
    public Message.Inbound create(final AsciiString content) {
        final TagDecoder tagDecoder = fixDecoder.wrap(content);
        headerHandler.reset();
        while (tagDecoder.hasNext() && headerHandler.msgType == null) {
            tagDecoder.tag(headerHandler);
        }

        final SpecMessage specMessage = cache.get(headerHandler.msgType);
        return specMessage.asInbound(content);
    }

    private final class HeaderHandler implements ValueHandler, Appendable {

        private final MsgType.SearchByCode searchByCode = new MsgType.SearchByCode();

        private MsgType msgType;

        @Override
        public void handle(final int tag, final ValueDecoder valueDecoder) {
            if (tag == 35) {
                searchByCode.reset();
                valueDecoder.getString(this);
                msgType = searchByCode.find();
            }
        }

        @Override
        public Appendable append(CharSequence csq) throws IOException {
            return null;
        }

        @Override
        public Appendable append(CharSequence csq, int start, int end) throws IOException {
            return null;
        }

        @Override
        public Appendable append(final char c) throws IOException {
            searchByCode.refine(c);
            return this;
        }

        public void reset() {
            searchByCode.reset();
            msgType = null;
        }
    }

}
