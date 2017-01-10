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
package org.fix4j.client.spec;

import org.fix4j.engine.Message;
import org.fix4j.engine.session.SessionLifecycle;
import org.fix4j.engine.session.SessionManagement;
import org.fix4j.engine.session.SessionMessageHandler;

/**
 * Created by ryan on 14/12/16.
 */
public final class Fix4jSessionMessageHandler implements SessionMessageHandler {

    @Override
    public void onMessage(final SessionManagement sessionManagement, final Message.Decodable message) {
        final MsgType msgType = message.msgType();
        switch (msgType) {
            case LOGON: {
                final LogonMessage.Decoder decoder = message.as(LogonMessage.Decoder.class);
                System.out.println("Received Logon");
                sessionManagement.loggedOn();
                break;
            }
            case TEST_REQUEST: {
                final TestRequest.Decoder decoder = message.as(TestRequest.Decoder.class);
                System.out.println("Received TestRequest");
                sessionManagement.heartbeat(decoder.testReqId());
                break;
            }
        }

    }
}
