package org.fix4j.engine.codec;

import org.fix4j.engine.type.AsciiString;

import java.io.IOException;

/**
 * Created by ryan on 12/12/16.
 */
public class FixDecoder implements TagDecoder, ValueDecoder {

    private AsciiString content;

    private int tag;

    private int index;

    public TagDecoder wrap(final AsciiString content) {
        this.content = content;
        index = 0;
        return this;
    }

    @Override
    public TagDecoder tag(final ValueHandler valueHandler) {
        int tag = 0;
        byte c;
        for (int i = 0; index < content.length(); i++) {
            c = content.byteAt(index++);
            if (c == '=') {
                break;
            }
            tag = (tag * 10) + (c - '0');
        }
        valueHandler.handle(tag, this);
        return this;
    }

    @Override
    public TagDecoder getString(Appendable appendable) {
        byte c;
        for (int i = 0; index < content.length(); i++) {
            c = content.byteAt(index++);
            if (c == '\u0001') {
                break;
            }
            try {
                appendable.append((char) c);
            } catch (IOException e) {
                // nothing right now.
            }
        }
        return this;
    }
}
