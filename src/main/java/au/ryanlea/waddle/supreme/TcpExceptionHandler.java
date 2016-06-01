package au.ryanlea.waddle.supreme;

import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Created by ryan on 1/06/16.
 */
public interface TcpExceptionHandler {

    void onError(IOException ioe);

    void onError(FixSessionConnection fixSessionConnection, IOException ioe);

    static TcpExceptionHandler throwing() {
        return new TcpExceptionHandler() {
            @Override
            public void onError(IOException ioe) {
                throw new SupremeWaddleException(ioe);
            }

            @Override
            public void onError(FixSessionConnection fixSessionConnection, IOException ioe) {
                throw new SupremeWaddleException(ioe);
            }
        };
    }

}
