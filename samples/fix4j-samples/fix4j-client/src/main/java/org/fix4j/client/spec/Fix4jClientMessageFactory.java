package org.fix4j.client.spec;

import org.fix4j.engine.Message;
import org.fix4j.engine.MessageFactory;
import org.fix4j.engine.codec.FixDecoder;
import org.fix4j.engine.codec.ValueDecoder;
import org.fix4j.engine.codec.ValueHandler;
import org.fix4j.engine.type.AsciiString;

import java.io.IOException;

public class Fix4jClientMessageFactory implements MessageFactory {

    private final FixDecoder fixDecoder = new FixDecoder();

    private final MessageTypeHandler messageTypeHandler = new MessageTypeHandler();

    @Override
    public Message create(final AsciiString message) {
        fixDecoder.wrap(message).tag(messageTypeHandler);
        System.out.println(messageTypeHandler.msgType);
        return null;
    }

    private final class MessageTypeHandler implements ValueHandler, Appendable {

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
