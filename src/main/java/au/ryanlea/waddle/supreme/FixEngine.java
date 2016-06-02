package au.ryanlea.waddle.supreme;

import org.jetbrains.annotations.NotNull;

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

    public FixEngine register(@NotNull FixSession fixSession) {
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

                    // read input messages into log
                    tcpConnectionHandler.fromWire();

                    // process messages (session and application)
                    for (int i = 0; i < fixSessions.size(); i++) {
                        fixSessions.get(i).process();
                    }

                    // write output messages from log
                    tcpConnectionHandler.toWire();
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
