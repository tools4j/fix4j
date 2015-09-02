/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 fix4j.org (tools4j.org)
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
package org.fix4j.sketch;

public class Example {
    public static void main(final String[] args) {
        final Dictionary<Fix42MessageType> fix42Dictionary = SimpleDictionary.define(dictionary -> {
            dictionary.defineMessage(Fix42MessageType.Heartbeat, heartbeat -> {
                heartbeat.defineField(Fix42Tags.TestRequestID);
            });

            dictionary.defineMessage(Fix42MessageType.TestRequest, testRequest -> {
                testRequest.defineField(Fix42Tags.TestRequestID, FieldDefinition::setRequired);
            });
        });

        final Message<Fix42MessageType> testRequest = fix42Dictionary.createMessage(Fix42MessageType.TestRequest);
        testRequest.set(Fix42Tags.TestRequestID, "TEST");

        switch (testRequest.messageType()) {
            case TestRequest:
                testRequest.get(Fix42Tags.TestRequestID, System.out::println);
                break;
        }
   }
}
