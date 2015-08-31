package org.fix4j.sketch;

import java.util.function.Consumer;
import java.util.function.Supplier;

public interface Message<T extends MessageType> {
    T messageType();

    boolean isSet(int tag);

    // I'm guessing at a minimum we need data types of:
    //      Char, String, int, long, boolean, "date" (as a long perhaps?)
    // Do we add get/set for each of these? That's a lot of methods :-O
    // Do we use generics on Tag? if so, won't that create a lot of boxing?

    String get(int tag);
    void get(int tag, Consumer<String> consumer);

    void set(int tag, String value);
    void set(int tag, Supplier<String> supplier);

    void copy(int tag, Message<T> from);
}
