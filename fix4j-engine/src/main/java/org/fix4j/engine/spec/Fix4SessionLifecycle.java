/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 fix4j.org (tools4j.org)
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
package org.fix4j.engine.spec;

import org.fix4j.engine.Message;
import org.fix4j.engine.session.FixSession;
import org.fix4j.engine.session.SessionLifecycle;
import org.fix4j.engine.session.SessionMessageFactory;
import org.fix4j.engine.session.SessionMessageHandler;

/**
 * Created by ryan on 23/06/16.
 */
public class Fix4SessionLifecycle {

    public enum State {
        NOT_LOGGED_ON, LOGON_SENT, LOGGED_ON
    }

    public static class Initiator implements SessionLifecycle {

        private State state = State.NOT_LOGGED_ON;

        private final SessionMessageFactory messageFactory;

        private final SessionMessageHandler messageHandler;

        public Initiator(final SessionMessageFactory messageFactory, final SessionMessageHandler messageHandler) {
            this.messageFactory = messageFactory;
            this.messageHandler = messageHandler;
        }

        @Override
        public void manage(final FixSession fixSession) {
            switch (state) {
                case NOT_LOGGED_ON:
                    fixSession.send(messageFactory.create(FixSession.MessageType.LOGON));
                    state = State.LOGON_SENT;
                    break;
                case LOGON_SENT:
                    // todo - check timing to see if we should timeout or error or something
                    break;
            }

        }

        @Override
        public void onMessage(final Message.Decodable message) {
            messageHandler.onMessage(message);
        }

    }

    public static class Acceptor implements SessionLifecycle{

        @Override
        public void manage(FixSession fixSession) {

        }

        @Override
        public void onMessage(Message.Decodable message) {

        }

    }

}
