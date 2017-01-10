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
package org.fix4j.engine.session;

public class FixSessionConfiguration {

    private CharSequence senderCompId;

    private CharSequence targetCompId;

    private CharSequence store;

    private int heartbeatInterval;

    public FixSessionConfiguration senderCompId(final CharSequence senderCompId) {
        this.senderCompId = senderCompId;
        return this;
    }

    public FixSessionConfiguration targetCompId(final CharSequence targetCompId) {
        this.targetCompId = targetCompId;
        return this;
    }

    public FixSessionConfiguration heartbeatInterval(final int heartbeatInterval) {
        this.heartbeatInterval = heartbeatInterval;
        return this;
    }

    public FixSessionConfiguration store(final CharSequence store) {
        this.store = store;
        return this;
    }

    public CharSequence senderCompId() {
        return senderCompId;
    }

    public CharSequence targetCompId() {
        return targetCompId;
    }

    public int heartbeatInterval() {
        return heartbeatInterval;
    }

    public CharSequence store() {
        return store;
    }
}
