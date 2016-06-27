package au.ryanlea.waddle.supreme.session;

import au.ryanlea.waddle.supreme.Message;
import au.ryanlea.waddle.supreme.log.LogEntry;

import java.util.function.Consumer;

/**
 * Created by ryan on 22/06/16.
 */
public interface SessionLifecycle {

    void manage(FixSession fixSession);

    Consumer<LogEntry> consume();
}
