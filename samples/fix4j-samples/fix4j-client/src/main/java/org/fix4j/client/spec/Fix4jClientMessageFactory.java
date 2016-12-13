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
    public Message.Decodable create(final AsciiString content) {
        TagDecoder tagDecoder = fixDecoder.wrap(content);
        while (tagDecoder.hasNext() && headerHandler.msgType == null) {
            tagDecoder.tag(headerHandler);
        }

        final SpecMessage specMessage = cache.get(headerHandler.msgType);
        return specMessage.decodable(content);
    }

    private final class HeaderHandler implements ValueHandler, Appendable {

        private final MsgType.SearchByCode searchByCode = new MsgType.SearchByCode();

        private MsgType msgType;

        @Override
        public void handle(final int tag, final ValueDecoder valueDecoder) {
            if (tag == 35) {
                searchByCode.reset();
                valueDecoder.getString(this);
                msgType = searchByCode.find().msgType();
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
    }

}
