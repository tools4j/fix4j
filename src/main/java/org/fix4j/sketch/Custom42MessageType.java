package org.fix4j.sketch;

import java.util.Objects;

public enum Custom42MessageType implements MessageType {
    Heartbeat("0"),
    TestRequest("1"),
    ResendRequest("2"),
    Reject("3"),
    SequenceReset("4"),
    Logout("5"),
    Logon("A");

    private final String tagValue;

    Custom42MessageType(final String tagValue) {
        this.tagValue = Objects.requireNonNull(tagValue);
    }

    @Override
    public String tagValue() {
        return tagValue;
    }
}
