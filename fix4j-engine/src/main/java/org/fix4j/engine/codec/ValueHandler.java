package org.fix4j.engine.codec;

@FunctionalInterface
public interface ValueHandler {

    void handle(int tag, ValueDecoder valueDecoder);
}
