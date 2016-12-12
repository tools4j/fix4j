package org.fix4j.client.spec;

import java.util.Arrays;
import java.util.Comparator;

public enum MsgType {
    LOGON
    ;

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
            upper = sorted.length + 1;
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
