package au.ryanlea.waddle.supreme.spec;

import au.ryanlea.waddle.supreme.Message;
import au.ryanlea.waddle.supreme.log.LogEntry;
import au.ryanlea.waddle.supreme.session.FixSession;
import au.ryanlea.waddle.supreme.session.SessionLifecycle;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Created by ryan on 23/06/16.
 */
public class Fix4SessionLifecycle {

    public enum State {
        NOT_LOGGED_ON, LOGON_SENT, LOGGED_ON
    }

    public enum SessionMessageType {
        HEARTBEAT, TEST_REQUEST, RESEND_REQUEST, REJECT, SEQUENCE_RESET, LOGOUT, LOGON,
    }

    public static class Initiator implements SessionLifecycle, Consumer<LogEntry> {

        private State state = State.NOT_LOGGED_ON;

        private final Function<SessionMessageType, Message> messageFactory;

        public Initiator(Function<SessionMessageType, Message> messageFactory) {
            this.messageFactory = messageFactory;
        }

        @Override
        public void manage(FixSession fixSession) {
            switch (state) {
                case NOT_LOGGED_ON:
                    fixSession.send(messageFactory.apply(SessionMessageType.LOGON));
                    state = State.LOGON_SENT;
                    break;
                case LOGON_SENT:
                    // todo - check timing to see if we should timeout or error or something
                    break;
            }

        }

        @Override
        public Consumer<LogEntry> consume() {
            return this;
        }

        @Override
        public void accept(LogEntry logEntry) {

        }
    }

    public static class Acceptor implements SessionLifecycle, Consumer<LogEntry> {

        @Override
        public void manage(FixSession fixSession) {

        }

        @Override
        public Consumer<LogEntry> consume() {
            return this;
        }

        @Override
        public void accept(LogEntry logEntry) {

        }
    }

}
