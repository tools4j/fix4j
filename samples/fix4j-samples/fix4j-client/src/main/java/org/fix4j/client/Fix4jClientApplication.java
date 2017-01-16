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
package org.fix4j.client;

import org.fix4j.client.spec.MarketDataRequest;
import org.fix4j.client.spec.MsgType;
import org.fix4j.client.spec.Reject;
import org.fix4j.engine.Application;
import org.fix4j.engine.Message;
import org.fix4j.engine.session.FixSession;
import org.fix4j.engine.session.FixSessionConfiguration;
import org.fix4j.engine.type.AsciiString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

/**
 * Created by ryan on 14/12/16.
 */
final class Fix4jClientApplication implements Application {

    private static final Logger log = LoggerFactory.getLogger(Fix4jClientApplication.class);

    private final FixSessionConfiguration fixSessionConfiguration;

    public Fix4jClientApplication(FixSessionConfiguration fixSessionConfiguration) {
        this.fixSessionConfiguration = fixSessionConfiguration;
    }

    @Override
    public void onMessage(final Message.Decodable message, final FixSession fixSession) {
        final MsgType msgType = message.msgType();
        switch (msgType) {
            case LOGON:
                subscribe(fixSession);
                break;
            case REJECT:
                final Reject.Decoder reject = message.as(Reject.Decoder.class);
                System.out.println(String.format("Received reject for [%s:%s]: [%s]", reject.refMsgType(), reject.refTagId(), reject.text()));
        }
    }

    private void subscribe(final FixSession fixSession) {
        final MarketDataRequest marketDataRequest = new MarketDataRequest();
        marketDataRequest.header()
                .senderCompId(fixSessionConfiguration.senderCompId())
                .targetCompId(fixSessionConfiguration.targetCompId());

        marketDataRequest.mdReqId(new AsciiString.Mutable(UUID.randomUUID().toString()));
        marketDataRequest.symbol(new AsciiString.Mutable("AUD/USD"));
        marketDataRequest.subscriptionRequestType('1');
        fixSession.send(marketDataRequest.encodable());
    }
}
