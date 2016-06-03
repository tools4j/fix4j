package au.ryanlea.waddle.supreme;

/**
 * Created by ryan on 3/06/16.
 */
public interface Buffer {
    long remaining();

    byte getByte(int pos);
}
