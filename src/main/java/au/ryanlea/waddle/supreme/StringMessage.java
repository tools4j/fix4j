package au.ryanlea.waddle.supreme;

/**
 * Created by ryan on 3/06/16.
 */
public class StringMessage implements Message {

    private final String content;

    public StringMessage(final String content) {
        this.content = content;
    }

    @Override
    public long remaining() {
        return content.length();
    }

    @Override
    public byte getByte(int pos) {
        return (byte) content.charAt(pos);
    }
}
