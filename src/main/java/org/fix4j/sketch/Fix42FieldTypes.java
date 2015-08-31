package org.fix4j.sketch;

public final class Fix42FieldTypes {
    public static final IntFieldType MsgSeqNum = new IntFieldType();
    public static final StringFieldType TestRequestID = new StringFieldType();

    private Fix42FieldTypes() {
        throw new IllegalStateException("No org.fix4j.sketch.Fix42Tags for you!");
    }
}
