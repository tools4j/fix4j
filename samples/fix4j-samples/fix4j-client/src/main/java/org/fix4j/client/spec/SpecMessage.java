package org.fix4j.client.spec;

import org.fix4j.engine.Message;
import org.fix4j.engine.type.AsciiString;

public interface SpecMessage extends Message {

    Message.Decodable decodable(AsciiString content);

}
