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
package org.fix4j.client.spec;

import org.fix4j.engine.Message;
import org.fix4j.engine.session.FixSession;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Optional;
import java.util.function.Supplier;

public enum MsgType implements Message.Type {
    LOGON("A", LogonMessage::new, FixSession.MessageType.LOGON),
    TEST_REQUEST("1", TestRequest::new, FixSession.MessageType.TEST_REQUEST);

    private String code;

    private final Supplier<SpecMessage> factory;

    private final FixSession.MessageType sessionMessageType;

    MsgType(final String code, final Supplier<SpecMessage> factory, final FixSession.MessageType sessionMessageType) {
        this.code = code;
        this.factory = factory;
        this.sessionMessageType = sessionMessageType;
    }

    public String code() {
        return code;
    }

    public SpecMessage message() {
        return factory.get();
    }

    @Override
    public Optional<FixSession.MessageType> asSessionType() {
        return Optional.ofNullable(sessionMessageType);
    }

    public static class SearchByCode {

        private final MsgType[] sorted = Arrays.copyOf(MsgType.values(), MsgType.values().length);

        private int numRefinements;

        private int lower;

        private int upper;

        public SearchByCode() {
            Arrays.sort(sorted, Comparator.comparing(MsgType::code));
        }

        public void reset() {
            numRefinements = 0;
            lower = -1;
            upper = sorted.length;
        }

        public MsgType find() {
            return sorted[lower];
        }

        public SearchByCode refine(char refine) {
            final int l = lower == -1 ? 0 : lower;
            final int u = upper;
            for (int i = l; i < u; i++) {
                final MsgType msgType = sorted[i];
                final String code = msgType.code();
                char c = code.charAt(numRefinements);
                if (c == refine && (lower == l || lower == -1)) {
                    lower = i;
                } else if (c > refine && upper == u) {
                    upper = i;
                }
            }
            numRefinements += 1;
            return this;
        }
    }

}
