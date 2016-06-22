package au.ryanlea.waddle.supreme.session;

import java.util.function.Consumer;

/**
 * Created by ryan on 22/06/16.
 */
public interface FixSessionConnection {
    FixSessionConnection establish();

    FixSessionConnection connect(Consumer<FixSession> onFixSession);
}
