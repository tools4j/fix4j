package org.fix4j.engine;

import org.fix4j.engine.type.AsciiString;

/**
 * Created by ryan on 12/12/16.
 */
public interface MessageFactory {

    Message create(AsciiString message);
}
