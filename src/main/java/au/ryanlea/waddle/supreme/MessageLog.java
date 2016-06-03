package au.ryanlea.waddle.supreme;

/**
 * Created by ryan on 2/06/16.
 */
public interface MessageLog {

    MessageLog readFrom(Buffer buffer);

    MessageLog writeTo(Buffer buffer);
}
