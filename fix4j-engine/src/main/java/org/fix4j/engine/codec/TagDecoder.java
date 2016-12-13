package org.fix4j.engine.codec;

/**
 * Created by ryan on 12/12/16.
 */
public interface TagDecoder {

    TagDecoder tag(ValueHandler valueHandler);

    boolean hasNext();
}
