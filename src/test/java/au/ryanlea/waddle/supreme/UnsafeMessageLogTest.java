package au.ryanlea.waddle.supreme;

import org.HdrHistogram.ConcurrentHistogram;
import org.HdrHistogram.Histogram;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by ryan on 3/06/16.
 */
public class UnsafeMessageLogTest {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    final Histogram histogram = new ConcurrentHistogram(TimeUnit.SECONDS.toNanos(1), 2);

    @Test
    public void log() throws IOException, InterruptedException {
        warmup(10);
        perform(20);
        histogram.outputPercentileDistribution(System.out, 1000.0);
    }

    private void perform(final int iterations) throws IOException, InterruptedException {
        for (int i = 0; i < iterations; i++) {
            execute(2, 100000, true);
        }
    }

    private void warmup(final int iterations) throws IOException, InterruptedException {
        for (int i = 0; i < iterations; i++) {
            execute(2, 1000, false);
        }
    }

    private void execute(final int threads, final int messages, final boolean record) throws IOException, InterruptedException {
        final File inbound = temporaryFolder.newFile("inbound-" + System.currentTimeMillis());
        final UnsafeMessageLog unsafeMessageLog = new UnsafeMessageLog(inbound.getAbsolutePath(), 100 * 1024 * 1024);
        final ExecutorService executorService = Executors.newFixedThreadPool(threads);
        for (int i = 0; i < threads; i++) {
            executorService.submit(() -> {
                long tid = Thread.currentThread().getId();
                final StringBuilder sb = new StringBuilder();
                for (int j = 0; j < messages; j++) {
                    sb.setLength(0);
                    sb.append("This is message [").append(j).append("] from thread [").append(tid).append("].\n");
                    final byte[] message = sb.toString().getBytes();
                    if (record) {
                        final long start = System.nanoTime();
                        unsafeMessageLog.log(message);
                        final long end = System.nanoTime();
                        histogram.recordValue(end - start);
                    } else {
                        unsafeMessageLog.log(message);
                    }
                }
            });
        }

        executorService.shutdown();
        executorService.awaitTermination(30, TimeUnit.SECONDS);
    }

}