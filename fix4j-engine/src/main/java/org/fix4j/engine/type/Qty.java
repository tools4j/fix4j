package org.fix4j.engine.type;

public class Qty {

    private double value;

    public double value() {
        return value;
    }

    public Qty value(final double value) {
        this.value = value;
        return this;
    }

}
