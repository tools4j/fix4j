package org.fix4j.sketch;

public class Example {
    public static void main(final String[] args) {
        final Dictionary<Fix42MessageType> fix42Dictionary = Dictionary.of(Fix42MessageType.class);
        final Message<Fix42MessageType> fix42Message = fix42Dictionary.parse("");

        switch (fix42Message.messageType()) {
            case Heartbeat:
                break;
        }

        final Dictionary<Custom42MessageType> custom42Dictionary = Dictionary.of(Custom42MessageType.class);
        final Message<Custom42MessageType> custom42Message = custom42Dictionary.parse("");

        switch (custom42Message.messageType()) {
            case Heartbeat:
                break;
        }
    }
}
