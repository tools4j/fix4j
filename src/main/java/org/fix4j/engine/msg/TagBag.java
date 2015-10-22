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
package org.fix4j.engine.msg;

import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;
import java.util.function.DoubleSupplier;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;
import java.util.function.LongConsumer;
import java.util.function.LongSupplier;
import java.util.function.Supplier;

import org.decimal4j.api.Decimal;
import org.decimal4j.factory.Factories;
import org.fix4j.engine.exception.InvalidValueException;
import org.fix4j.engine.exception.NoSuchTagException;
import org.fix4j.engine.tag.BooleanTag;
import org.fix4j.engine.tag.CharTag;
import org.fix4j.engine.tag.DecimalTag;
import org.fix4j.engine.tag.DoubleTag;
import org.fix4j.engine.tag.FixTag;
import org.fix4j.engine.tag.GroupTag;
import org.fix4j.engine.tag.IntTag;
import org.fix4j.engine.tag.LongTag;
import org.fix4j.engine.tag.ObjectTag;
import org.fix4j.engine.tag.StringTag;
import org.fix4j.engine.validate.Validity;

public interface TagBag {
    boolean isSet(FixTag tag);
    Validity isValid(FixTag tag);

    String getString(FixTag tag) throws NoSuchTagException;
    void setString(FixTag tag, String value) throws InvalidValueException;

    void setString(StringTag tag, String value);
    void setInt(IntTag tag, int value);
    void setLong(LongTag tag, long value);
    void setDouble(DoubleTag tag, double value);
    void setDecimal(DecimalTag tag, Decimal<?> value) throws InvalidValueException;
    void setDecimalUnscaled(DecimalTag tag, long value);
    void setChar(CharTag tag, char value);
    void setBoolean(BooleanTag tag, boolean value);
    <T> void setObject(ObjectTag<T> tag, T value);

    List<TagBag> getGroup(GroupTag tag) throws NoSuchTagException, InvalidValueException;
	void setGroup(GroupTag tag, List<TagBag> value);

    void copy(FixTag tag, TagBag from) throws NoSuchTagException, InvalidValueException;
    
	default int getInt(final IntTag tag) throws NoSuchTagException, InvalidValueException {
		final String s = getString(tag);
		return tag.convertFrom(s, 0, s.length());
	}
	default long getLong(LongTag tag) throws NoSuchTagException, InvalidValueException {
		final String s = getString(tag);
		return tag.convertFrom(s, 0, s.length());
	}
    default double getDouble(DoubleTag tag) throws NoSuchTagException, InvalidValueException {
		final String s = getString(tag);
		return tag.convertFrom(s, 0, s.length());
    }
    default Decimal<?> getDecimal(DecimalTag tag) throws NoSuchTagException, InvalidValueException {
    	final long unscaled = getDecimalUnscaled(tag);
		return Factories.getDecimalFactory(tag.getPrecision()).valueOfUnscaled(unscaled);
    }
    default long getDecimalUnscaled(DecimalTag tag) throws NoSuchTagException, InvalidValueException {
		final String s = getString(tag);
		return tag.convertFrom(s, 0, s.length());
    }
    default char getChar(CharTag tag) throws NoSuchTagException, InvalidValueException {
		final String s = getString(tag);
		return tag.convertFrom(s, 0, s.length());
    }
    default boolean getBoolean(BooleanTag tag) throws NoSuchTagException, InvalidValueException {
		final String s = getString(tag);
		return tag.convertFrom(s, 0, s.length());
    }
    default <T> T getObject(ObjectTag<T> tag) throws NoSuchTagException, InvalidValueException {
		final String s = getString(tag);
		return tag.convertFrom(s, 0, s.length());
    }

    default void getString(FixTag tag, Consumer<? super String> consumer) throws NoSuchTagException {
    	consumer.accept(getString(tag));
    }
    default void getInt(IntTag tag, IntConsumer consumer) throws NoSuchTagException, InvalidValueException {
    	consumer.accept(getInt(tag));
    }
    default void getLong(LongTag tag, LongConsumer consumer) throws NoSuchTagException, InvalidValueException {
    	consumer.accept(getLong(tag));
    }
    default void getDouble(DoubleTag tag, DoubleConsumer consumer) throws NoSuchTagException, InvalidValueException {
    	consumer.accept(getDouble(tag));
    }
    default void getDecimal(DecimalTag tag, Consumer<? super Decimal<?>> consumer) throws NoSuchTagException, InvalidValueException {
    	consumer.accept(getDecimal(tag));
    }
    default void getDecimalUnscaled(DecimalTag tag, LongConsumer consumer) throws NoSuchTagException, InvalidValueException {
    	consumer.accept(getDecimalUnscaled(tag));
    }
    default void getChar(CharTag tag, IntConsumer consumer) throws NoSuchTagException, InvalidValueException {
    	consumer.accept(getChar(tag));
    }
    default void getBoolean(BooleanTag tag, Consumer<Boolean> consumer) throws NoSuchTagException, InvalidValueException {
    	consumer.accept(getBoolean(tag));
    }
    default <T> void getObject(ObjectTag<T> tag, Consumer<? super T> consumer) throws NoSuchTagException, InvalidValueException {
    	consumer.accept(getObject(tag));
    }
    default void getGroup(GroupTag tag, Consumer<? super List<TagBag>> consumer) throws NoSuchTagException, InvalidValueException {
    	consumer.accept(getGroup(tag));
    }
    
    default void setString(FixTag tag, Supplier<? extends String> supplier) throws InvalidValueException {
    	setString(tag, supplier.get());
    }
    default void setString(StringTag tag, Supplier<? extends String> supplier) {
    	setString(tag, supplier.get());
    }
    default void setInt(IntTag tag, IntSupplier supplier) {
    	setInt(tag, supplier.getAsInt());
    }
    default void setLong(LongTag tag, LongSupplier supplier) {
    	setLong(tag, supplier.getAsLong());
    }
    default void setDouble(DoubleTag tag, DoubleSupplier supplier) {
    	setDouble(tag, supplier.getAsDouble());
    }
    default void setDecimal(DecimalTag tag, Supplier<? extends Decimal<?>> supplier) throws InvalidValueException {
    	setDecimal(tag, supplier.get());
    }
    default void setDecimalUnscaled(DecimalTag tag, LongSupplier supplier) {
    	setDecimalUnscaled(tag, supplier.getAsLong());
    }
    default void setChar(CharTag tag, IntSupplier supplier) {
    	setChar(tag, (char)supplier.getAsInt());
    }
    default void setBoolean(BooleanTag tag, BooleanSupplier supplier) {
    	setBoolean(tag, supplier.getAsBoolean());
    }
    default <T> void setObject(ObjectTag<T> tag, Supplier<? extends T> supplier) {
    	setObject(tag, supplier.get());
    }
    default void setGroup(GroupTag tag, Supplier<? extends List<TagBag>> supplier) {
    	setGroup(tag, supplier.get());
    }

}
