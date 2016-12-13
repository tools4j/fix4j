package org.fix4j.engine.session;

import org.fix4j.engine.Message;

public interface SessionMessageFactory {

    Message.Encodable create(FixSession.MessageType messageType);

}
