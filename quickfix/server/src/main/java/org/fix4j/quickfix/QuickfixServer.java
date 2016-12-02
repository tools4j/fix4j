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
package org.fix4j.quickfix;

import quickfix.*;

/**
 * Created by ryan on 2/12/16.
 */
public class QuickfixServer {

    public static void main (String[] args) throws ConfigError {
        new QuickfixServer().run();
    }

    private final SocketAcceptor socketAcceptor;

    private QuickfixServer() throws ConfigError {
        final SessionSettings sessionSettings = new SessionSettings("server.session.settings");
        final Application application = new SimpleApplication();
        final MessageStoreFactory messageStoreFactory = new FileStoreFactory(sessionSettings);
        final MessageFactory messageFactory = new DefaultMessageFactory();
        socketAcceptor = new SocketAcceptor(application, messageStoreFactory, sessionSettings, messageFactory);
    }

    private void run() throws ConfigError {
        socketAcceptor.start();
        while (!Thread.interrupted()) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private final class SimpleApplication implements Application {

        @Override
        public void onCreate(SessionID sessionId) {

        }

        @Override
        public void onLogon(SessionID sessionId) {

        }

        @Override
        public void onLogout(SessionID sessionId) {

        }

        @Override
        public void toAdmin(Message message, SessionID sessionId) {

        }

        @Override
        public void fromAdmin(Message message, SessionID sessionId) throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, RejectLogon {

        }

        @Override
        public void toApp(Message message, SessionID sessionId) throws DoNotSend {

        }

        @Override
        public void fromApp(Message message, SessionID sessionId) throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, UnsupportedMessageType {

        }
    }
}
