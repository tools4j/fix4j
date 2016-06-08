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
package au.ryanlea.waddle.supreme;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by ryan on 1/06/16.
 */
public class FixEngine {

    private final AtomicReference<FixSession> fixSessionToAdd = new AtomicReference<>();

    private final List<FixSession> fixSessions = new ArrayList<>();

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    private final AtomicBoolean terminated = new AtomicBoolean(false);

    private final TcpConnectionHandler tcpConnectionHandler;

    private final ExceptionHandler exceptionHandler;

    public FixEngine(final TcpConnectionHandler tcpConnectionHandler, final ExceptionHandler exceptionHandler) {
        this.tcpConnectionHandler = tcpConnectionHandler;
        this.exceptionHandler = exceptionHandler;
    }

    public FixEngine register(FixSession fixSession) {
        while (true) {
            if (fixSessionToAdd.compareAndSet(null, fixSession)) {
                return this;
            }
        }
    }

    public Iterable<FixSession> fixSessions() {
        return fixSessions;
    }

    public FixEngine start() {
        executorService.submit(() -> {
            try {
                while (!terminated.get()) {
                    addNewFixSession();

                    // establish new sessions
                    establishFixSessions();

                    // process messages (session and application)
                    for (int i = 0; i < fixSessions.size(); i++) {
                        final FixSession fixSession = fixSessions.get(i);
                        fixSession.fromWire();
                        fixSession.process();
                        fixSession.toWire();
                    }

                }
            } catch (Exception e) {
                exceptionHandler.onError(e);
            }
        });
        return this;
    }

    private void establishFixSessions() {
        for (int i = 0; i < fixSessions.size(); i++) {
            fixSessions.get(i).establish();
        }
        tcpConnectionHandler.selectAndConnect();
    }

    private void addNewFixSession() {
        FixSession fixSession = fixSessionToAdd.getAndSet(null);
        if (fixSession != null) {
            fixSessions.add(fixSession);
        }
    }

    public FixEngine terminate(final long timeout, final TimeUnit timeUnit) {
        terminated.set(false);
        try {
            executorService.shutdown();
            if (!executorService.awaitTermination(timeout, timeUnit)) {
                executorService.shutdownNow();
            }
        } catch (Throwable t) {
            executorService.shutdownNow();
        }
        return this;
    }
}
