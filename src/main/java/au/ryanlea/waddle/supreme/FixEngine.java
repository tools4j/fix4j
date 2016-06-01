package au.ryanlea.waddle.supreme;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by ryan on 1/06/16.
 */
public class FixEngine {

    private final AtomicReference<FixSessionConnection> connectionToAdd = new AtomicReference<>();

    private final List<FixSessionConnection> fixSessionConnections = new ArrayList<>();

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    private final AtomicBoolean terminated = new AtomicBoolean(false);

    private final TcpConnectionHandler tcpConnectionHandler;

    private final ExceptionHandler exceptionHandler;

    public FixEngine(final TcpConnectionHandler tcpConnectionHandler, final ExceptionHandler exceptionHandler) {
        this.tcpConnectionHandler = tcpConnectionHandler;
        this.exceptionHandler = exceptionHandler;
    }

    public FixEngine register(@NotNull FixSessionConnection fixSessionConnection) {
        while (true) {
            if (connectionToAdd.compareAndSet(null, fixSessionConnection)) {
                return this;
            }
        }
    }

    public Iterable<FixSessionConnection> acceptorFixSessions() {
        return fixSessionConnections;
    }

    public FixEngine start() {
        executorService.submit(() -> {
            try {
                while (!terminated.get()) {
                    addNewConnection();
                    for (int i = 0; i < fixSessionConnections.size(); i++) {
                        fixSessionConnections.get(i).establish(tcpConnectionHandler);
                    }
                    tcpConnectionHandler.handle();
                }
            } catch (Exception e) {
                exceptionHandler.onError(e);
            }
        });
        return this;
    }

    private void addNewConnection() {
        FixSessionConnection fixSessionConnection = connectionToAdd.getAndSet(null);
        if (fixSessionConnection != null) {
            fixSessionConnections.add(fixSessionConnection);
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
