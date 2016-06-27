package au.ryanlea.waddle.supreme;

import au.ryanlea.waddle.supreme.log.LogEntry;

import java.util.function.Consumer;

/**
 * Created by ryan on 23/06/16.
 */
public interface Application {

    Consumer<LogEntry> consume();

}
