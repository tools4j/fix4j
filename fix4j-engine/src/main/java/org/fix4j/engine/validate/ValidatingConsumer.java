/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-2017 fix4j.org (tools4j.org)
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
package org.fix4j.engine.validate;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.fix4j.engine.exception.InvalidValueException;
import org.fix4j.engine.stream.TagValueConsumer;
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

public class ValidatingConsumer implements TagValueConsumer {
	
	private final Map<FixTag, Validity> validities = new ConcurrentHashMap<>();
	
	public Validity isValid(final FixTag tag) {
		return validities.get(tag);
	}
	
	@Override
	public void acceptBoolean(final BooleanTag tag, boolean value) {
		validities.putIfAbsent(tag, Validity.VALID);
	}

	@Override
	public void acceptChar(final CharTag tag, final char value) {
		validities.putIfAbsent(tag, Validity.VALID);
	}

	@Override
	public void acceptInt(IntTag tag, int value) {
		validities.putIfAbsent(tag, Validity.VALID);
	}

	@Override
	public void acceptLong(LongTag tag, long value) {
		validities.putIfAbsent(tag, Validity.VALID);
	}

	@Override
	public void acceptDouble(DoubleTag tag, double value) {
		validities.putIfAbsent(tag, Validity.VALID);
	}

	@Override
	public void acceptDecimal(DecimalTag tag, long value) {
		validities.putIfAbsent(tag, Validity.VALID);
	}

	@Override
	public void acceptString(StringTag tag, CharSequence value) {
		validities.putIfAbsent(tag, Validity.VALID);
	}

	@Override
	public void acceptGroup(GroupTag tag, int groupSize) {
		validities.putIfAbsent(tag, Validity.VALID);
	}

	@Override
	public <T> void acceptObject(ObjectTag<T> tag, T value) {
		validities.putIfAbsent(tag, Validity.VALID);
	}

	@Override
	public void acceptOther(FixTag tag, CharSequence value) {
		validities.putIfAbsent(tag, Validity.VALID);
	}
	
	@Override
	public void invalidValue(FixTag tag, InvalidValueException exception) throws InvalidValueException {
		validities.putIfAbsent(tag, Validity.INVALID_WITH_MESSAGE.apply(exception.getMessage()));
	}

}
