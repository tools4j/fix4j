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
package org.fix4j.engine.spec;

import org.fix4j.engine.Message;
import org.fix4j.engine.session.*;
import org.fix4j.engine.type.AsciiString;

/**
 * Created by ryan on 23/06/16.
 */
public class Fix4SessionLifecycle {

    public enum State {
        NOT_LOGGED_ON, LOGON_SENT, LOGGED_ON
    }

    public static class Initiator implements SessionLifecycle, SessionLifecycle.Bound, SessionManagement {

        private State state = State.NOT_LOGGED_ON;

        private final SessionMessageFactory messageFactory;

        private final SessionMessageHandler messageHandler;

        private transient FixSession fixSession;

        private Message.Outbound heartbeat;

        public Initiator(final SessionMessageFactory messageFactory, final SessionMessageHandler messageHandler) {
            this.messageFactory = messageFactory;
            this.messageHandler = messageHandler;
        }

        @Override
        public void manage() {
            switch (state) {
                case NOT_LOGGED_ON:
                    fixSession.send(messageFactory.create(FixSession.MessageType.LOGON));
                    state = State.LOGON_SENT;
                    break;
                case LOGON_SENT:
                    // todo - check timing to see if we should timeout or error or something
                    break;
                case LOGGED_ON:
                    // todo - send heartbeat if one is not sent or test request if we need to
                    break;
            }

        }

        @Override
        public void onMessage(final Message.Inbound message) {
            messageHandler.onMessage(this, message);
        }

        @Override
        public void loggedOn() {
            state = State.LOGGED_ON;
        }

        @Override
        public void heartbeat(final AsciiString testReqId) {
            System.out.print("Send Heartbeat");
            fixSession.send(messageFactory.heartbeat(testReqId));
        }

        @Override
        public Bound bind(final FixSession fixSession) {
            this.fixSession = fixSession;
            return this;
        }
    }

}
