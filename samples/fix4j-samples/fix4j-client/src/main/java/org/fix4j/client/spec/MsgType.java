package org.fix4j.client.spec;

import org.fix4j.engine.Message;
import org.fix4j.engine.session.FixSession;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Optional;
import java.util.function.Supplier;

public enum MsgType implements Message.Type {
    LOGON(LogonMessage::new, FixSession.MessageType.LOGON);

    private final Supplier<SpecMessage> factory;

    private final FixSession.MessageType sessionMessageType;

    MsgType(Supplier<SpecMessage> factory, FixSession.MessageType sessionMessageType) {
        this.factory = factory;
        this.sessionMessageType = sessionMessageType;
    }

    public SpecMessage message() {
        return factory.get();
    }

    @Override
    public Optional<FixSession.MessageType> asSessionType() {
        return Optional.ofNullable(sessionMessageType);
    }


    public enum MsgCode {
        A(MsgType.LOGON)
        ;

        private final MsgType msgType;

        MsgCode(final MsgType msgType) {
            this.msgType = msgType;
        }

        public MsgType msgType() {
            return msgType;
        }
    }

    public static class SearchByCode {

        private final MsgCode[] sorted = Arrays.copyOf(MsgCode.values(), MsgCode.values().length);

        private int numRefinements;

        private int lower;

        private int upper;

        public SearchByCode() {
            Arrays.sort(sorted, Comparator.comparing(Enum::name));
        }

        public void reset() {
            numRefinements = 0;
            lower = 0;
            upper = sorted.length;
        }

        public MsgCode find() {
            return sorted[lower];
        }

        public SearchByCode refine(char refine) {
            final int l = lower;
            final int u = upper;
            for (int i = l; i < u; i++) {
                final MsgCode msgCode = sorted[i];
                final String name = msgCode.name();
                char c = name.charAt(numRefinements);
                if (c == refine && lower == l) {
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
