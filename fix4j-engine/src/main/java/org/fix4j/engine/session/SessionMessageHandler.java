package org.fix4j.engine.session;

import org.fix4j.engine.Message;

/**
 * Created by ryan on 13/12/16.
 */
public interface SessionMessageHandler {

    void onMessage(Message.Decodable message);

}
