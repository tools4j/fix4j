package au.ryanlea.waddle.supreme;

import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Created by ryan on 1/06/16.
 */
public interface ExceptionHandler {

    void onError(Exception e);

    static ExceptionHandler logging() {
        return new ExceptionHandler() {
            @Override
            public void onError(Exception ioe) {
                LoggerFactory.getLogger("").error("", ioe);
            }
        };
    }

}
