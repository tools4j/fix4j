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
package org.fix4j.engine.stream;

import org.fix4j.engine.exception.InvalidValueException;
import org.fix4j.engine.tag.BooleanTag;
import org.fix4j.engine.tag.CharTag;
import org.fix4j.engine.tag.DecimalTag;
import org.fix4j.engine.tag.DoubleTag;
import org.fix4j.engine.tag.FixTag;
import org.fix4j.engine.tag.IntTag;
import org.fix4j.engine.tag.LongTag;
import org.fix4j.engine.tag.ObjectTag;
import org.fix4j.engine.tag.StringTag;

public interface TagValueConsumer {
	void acceptBoolean(BooleanTag tag, boolean value);
	void acceptChar(CharTag tag, char value);
	void acceptInt(IntTag tag, int value);
	void acceptLong(LongTag tag, long value);
	void acceptDouble(DoubleTag tag, double value);
	void acceptDecimal(DecimalTag tag, long value);
	void acceptString(StringTag tag, CharSequence value);
	<T> void acceptObject(ObjectTag<T> tag, T value);
	void acceptOther(FixTag tag, CharSequence value);
	
	default void invalidValue(FixTag tag, InvalidValueException exception) throws InvalidValueException {
		throw exception;
	}
}
