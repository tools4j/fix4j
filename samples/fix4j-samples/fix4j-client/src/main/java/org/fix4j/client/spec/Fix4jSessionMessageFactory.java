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
import org.fix4j.engine.session.FixSession;
import org.fix4j.engine.session.FixSessionConfiguration;
import org.fix4j.engine.session.SessionMessageFactory;

/**
 * Created by ryan on 14/12/16.
 */
public class Fix4jSessionMessageFactory implements SessionMessageFactory {

    private final FixSessionConfiguration fixSessionConfiguration;

    public Fix4jSessionMessageFactory(FixSessionConfiguration fixSessionConfiguration) {
        this.fixSessionConfiguration = fixSessionConfiguration;
    }

    @Override
    public Message.Encodable create(FixSession.MessageType messageType) {
        switch (messageType) {
            case LOGON:
                final LogonMessage logonMessage = new LogonMessage();
                logonMessage.heartBtInt(fixSessionConfiguration.heartbeatInterval());
                logonMessage.header()
                        .senderCompId(fixSessionConfiguration.senderCompId())
                        .targetCompId(fixSessionConfiguration.targetCompId());
                return logonMessage.encodable();
        }
        return null;
    }
}
