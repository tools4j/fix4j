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
package org.fix4j.engine.log;

import org.fix4j.engine.ExceptionHandler;
import org.fix4j.engine.log.UnsafeMessageLog;
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
        final UnsafeMessageLog unsafeMessageLog = new UnsafeMessageLog(inbound.getAbsolutePath(), 100 * 1024 * 1024, ExceptionHandler.throwing());
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