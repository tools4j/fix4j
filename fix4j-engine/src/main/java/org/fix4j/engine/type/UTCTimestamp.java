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
package org.fix4j.engine.type;

import java.time.*;

public class UTCTimestamp {

    public static final int NANOS_IN_MILLI = 1_000_000;
    private long epochMillis;

    private LocalDateTime localDateTime;

    public UTCTimestamp epochMillis(final long epochMillis) {
        this.epochMillis = epochMillis;
        this.localDateTime = null;
        return this;
    }

    public long epochMillis() {
        return epochMillis;
    }

    public int year() {
        return localDateTime().getYear();
    }

    public int month() {
        return localDateTime().getMonthValue();
    }

    public int day() {
        return localDateTime().getDayOfMonth();
    }

    public int hour() {
        return localDateTime().getHour();
    }

    public int minute() {
        return localDateTime().getMinute();
    }

    public int second() {
        return localDateTime().getSecond();
    }

    public int millis() {
        return localDateTime().getNano() / NANOS_IN_MILLI;
    }


    private LocalDateTime localDateTime() {
        if (localDateTime == null) {
            localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(epochMillis), ZoneOffset.UTC);
        }
        return localDateTime;
    }
}
