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
package org.fix4j.quickfix;

import com.google.common.util.concurrent.RateLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickfix.*;
import quickfix.field.*;
import quickfix.fix44.MarketDataIncrementalRefresh;
import quickfix.fix44.MarketDataRequest;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by ryan on 2/12/16.
 */
public class QuickfixServer {

    private static final Logger logger = LoggerFactory.getLogger(QuickfixServer.class);

    public static void main (String[] args) throws ConfigError {
        new QuickfixServer().run();
    }

    private final SocketAcceptor socketAcceptor;

    private final MarketDataGenerator marketDataGenerator = new MarketDataGenerator();

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    private QuickfixServer() throws ConfigError {
        final SessionSettings sessionSettings = new SessionSettings("server.session.settings");
        final Application application = new SimpleApplication();
        final MessageStoreFactory messageStoreFactory = new FileStoreFactory(sessionSettings);
        final MessageFactory messageFactory = new DefaultMessageFactory();
        socketAcceptor = new SocketAcceptor(application, messageStoreFactory, sessionSettings, new FileLogFactory(sessionSettings), messageFactory);
        executorService.submit(marketDataGenerator);
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
            logger.info("onCreate for [{}]", sessionId);
        }

        @Override
        public void onLogon(SessionID sessionId) {
            logger.info("onLogon for [{}]", sessionId);
        }

        @Override
        public void onLogout(SessionID sessionId) {
            logger.info("onLogout for [{}]", sessionId);
        }

        @Override
        public void toAdmin(Message message, SessionID sessionId) {
            logger.info("toAdmin with [{}] for [{}]", message, sessionId);
        }

        @Override
        public void fromAdmin(Message message, SessionID sessionId) throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, RejectLogon {
            logger.info("fromAdmin with [{}] for [{}]", message, sessionId);
        }

        @Override
        public void toApp(Message message, SessionID sessionId) throws DoNotSend {
            logger.info("toApp with [{}] for [{}]", message, sessionId);
        }

        @Override
        public void fromApp(Message message, SessionID sessionId) throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, UnsupportedMessageType {
            logger.info("fromApp with [{}] for [{}]", message, sessionId);
            final String msgType = message.getHeader().getString(MsgType.FIELD);
            switch (msgType) {
                case MarketDataRequest.MSGTYPE: {
                    final String mdReqId = message.getString(MDReqID.FIELD);
                    final String symbol = message.getGroups(NoRelatedSym.FIELD).get(0).getString(Symbol.FIELD);
                    marketDataGenerator.subscribe(new Subscription(symbol, mdReqId, sessionId));
                }
            }
        }
    }

    private class Subscription {
        private String symbol;
        private String mdReqId;
        private SessionID sessionID;

        public Subscription(final String symbol, String mdReqId, final SessionID sessionID) {
            this.symbol = symbol;
            this.mdReqId = mdReqId;
            this.sessionID = sessionID;
        }
    }

    private class Mid {
        private final Random random = new Random(System.nanoTime());

        private double price;

        public Mid(final double price) {
            this.price = price;
        }

        public void move() {
            if (random.nextBoolean()) {
                price += 0.01;
            } else {
                price -= 0.01;
            }
        }
    }


    private class MarketDataGenerator implements Runnable {

        private final RateLimiter rateLimiter = RateLimiter.create(10.0);

        private final CopyOnWriteArrayList<Subscription> subscriptions = new CopyOnWriteArrayList<>();

        private final Map<String, Mid> mids = new HashMap<>();

        @Override
        public void run() {
            while (!Thread.interrupted()) {
                rateLimiter.acquire();
                for (final Subscription subscription : subscriptions) {
                    final Mid mid = mids.computeIfAbsent(subscription.symbol, s -> new Mid(1.00));
                    mid.move();

                    final MarketDataIncrementalRefresh marketDataIncrementalRefresh = new MarketDataIncrementalRefresh();
                    marketDataIncrementalRefresh.setString(MDReqID.FIELD, subscription.mdReqId);

                    final MarketDataIncrementalRefresh.NoMDEntries bid = new MarketDataIncrementalRefresh.NoMDEntries();
                    bid.setChar(MDUpdateAction.FIELD, MDUpdateAction.NEW);
                    bid.setString(Symbol.FIELD, subscription.symbol);
                    bid.setChar(MDEntryType.FIELD, MDEntryType.BID);
                    bid.setString(MDEntryID.FIELD, UUID.randomUUID().toString());
                    bid.setDouble(MDEntryPx.FIELD, mid.price - 0.001);
                    bid.setInt(MDEntrySize.FIELD, 1000000);
                    bid.setString(Currency.FIELD, subscription.symbol.substring(0, 3));
                    marketDataIncrementalRefresh.addGroup(bid);

                    final MarketDataIncrementalRefresh.NoMDEntries offer = new MarketDataIncrementalRefresh.NoMDEntries();
                    offer.setChar(MDUpdateAction.FIELD, MDUpdateAction.NEW);
                    offer.setString(Symbol.FIELD, subscription.symbol);
                    offer.setChar(MDEntryType.FIELD, MDEntryType.OFFER);
                    offer.setString(MDEntryID.FIELD, UUID.randomUUID().toString());
                    offer.setDouble(MDEntryPx.FIELD, mid.price + 0.001);
                    offer.setInt(MDEntrySize.FIELD, 1000000);
                    offer.setString(Currency.FIELD, subscription.symbol.substring(0, 3));
                    marketDataIncrementalRefresh.addGroup(offer);

                    try {
                        Session.sendToTarget(marketDataIncrementalRefresh, subscription.sessionID);
                    } catch (final SessionNotFound sessionNotFound) {
                        logger.error("Failed to send market update to session.", sessionNotFound);
                    }
                }
            }
        }

        public void subscribe(final Subscription subscription) {
            this.subscriptions.add(subscription);
        }
    }
}
