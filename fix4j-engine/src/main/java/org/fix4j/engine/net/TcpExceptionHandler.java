/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 fix4j.org (tools4j.org)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.fix4j.engine.net;

import org.fix4j.engine.ExceptionHandler;
import org.fix4j.engine.exception.Fix4jException;

import java.io.IOException;

/**
 * Created by ryan on 1/06/16.
 */
public interface TcpExceptionHandler extends ExceptionHandler {

    void onError(IOException ioe);

    void onError(TcpConnection tcpConnection, IOException ioe);

    static TcpExceptionHandler throwing() {
        return new TcpExceptionHandler() {
            @Override
            public void onError(Exception e) {
                throw new Fix4jException(e);
            }

            @Override
            public void onError(IOException ioe) {
                throw new Fix4jException(ioe);
            }

            @Override
            public void onError(TcpConnection tcpConnection, IOException ioe) {
                throw new Fix4jException(ioe);
            }
        };
    }

}
