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
package org.fix4j.engine.msg.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.decimal4j.api.Decimal;
import org.fix4j.engine.exception.InvalidValueException;
import org.fix4j.engine.exception.NoSuchTagException;
import org.fix4j.engine.msg.TagBag;
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
import org.fix4j.engine.validate.ValidatingConsumer;
import org.fix4j.engine.validate.Validity;

public class SimpleTagBag implements TagBag {
	
	private final SetterDispatcher setterDispatcher = new SetterDispatcher();	
	private final Map<FixTag, String> tagBag = new LinkedHashMap<>();
	private final Map<FixTag, List<TagBag>> groupBags = new LinkedHashMap<>();
	private final ValidatingConsumer validity = new ValidatingConsumer();

	@Override
	public boolean isSet(FixTag tag) {
		return tagBag.containsKey(tag);
	}
	@Override
	public Validity isValid(FixTag tag) {
		final Validity valid = validity.isValid(tag);
		return valid == null ? validate(tag) : valid; 
	}

	private Validity validate(final FixTag tag) {
		final String s = tagBag.get(tag);
		if (s == null) {
			return Validity.NO_SUCH_TAG;
		}
		try {
			tag.dispatch(s, validity);
			final Validity valid = validity.isValid(tag);
			return valid != null ? valid : Validity.INVALID;//should never be null
		} catch (InvalidValueException e) {
			//should not happen
			return Validity.INVALID_WITH_MESSAGE.apply(e.getMessage());
		}
	}

	@Override
	public String getString(final FixTag tag) throws NoSuchTagException {
		final String s = tagBag.get(tag);
		if (s != null) {
			return s;
		}
		throw new NoSuchTagException(tag);
	}

	@Override
	public void setString(FixTag tag, String value) throws InvalidValueException {
		tag.dispatch(value, setterDispatcher);
	}
	
	public void setString(StringTag tag, String value) {
		tagBag.put(tag, value);
	}

	@Override
	public void setInt(IntTag tag, int value) {
		try {
			final StringBuilder sb = new StringBuilder();
			tag.convertTo(value, sb);
			tagBag.put(tag, sb.toString());
		} catch (IOException e) {
			//should never happen
			throw new AssertionError(e);
		}
	}
	@Override
	public void setLong(LongTag tag, long value) {
		try {
			final StringBuilder sb = new StringBuilder();
			tag.convertTo(value, sb);
			tagBag.put(tag, sb.toString());
		} catch (IOException e) {
			//should never happen
			throw new AssertionError(e);
		}
	}
	@Override
	public void setDouble(DoubleTag tag, double value) {
		try {
			final StringBuilder sb = new StringBuilder();
			tag.convertTo(value, sb);
			tagBag.put(tag, sb.toString());
		} catch (IOException e) {
			//should never happen
			throw new AssertionError(e);
		}
	}
	@Override
	public void setDecimal(DecimalTag tag, Decimal<?> value) throws InvalidValueException {
		if (value.getScale() != tag.getPrecision()) {
			throw new InvalidValueException(tag, "expected precision " + tag.getPrecision() + " but value has scale " + value.getScale() + ": " + value);
		}
		setDecimalUnscaled(tag, value.unscaledValue());
	}
	@Override
	public void setDecimalUnscaled(DecimalTag tag, long value) {
		tagBag.put(tag, tag.convertToString(value));
	}
	@Override
	public void setChar(CharTag tag, char value) {
		tagBag.put(tag, tag.convertToString(value));
	}
	@Override
	public void setBoolean(BooleanTag tag, boolean value) {
		tagBag.put(tag, tag.convertToString(value));
	}
	@Override
	public <T> void setObject(ObjectTag<T> tag, T value) {
		tagBag.put(tag, tag.convertToString(value));
	}

	@Override
	public void copy(FixTag tag, TagBag from) throws NoSuchTagException, InvalidValueException {
		tagBag.put(tag, from.getString(tag));
		if (tag instanceof GroupTag) {
			groupBags.put(tag, from.getGroup((GroupTag)tag));
		}
	}

	@Override
	public void setGroup(GroupTag tag, List<TagBag> value) {
		tagBag.put(tag, String.valueOf(value.size()));
		groupBags.put(tag, Collections.unmodifiableList(new ArrayList<>(value)));
	}
	@Override
	public List<TagBag> getGroup(GroupTag tag) throws NoSuchTagException, InvalidValueException {
		final List<TagBag> groupBag = groupBags.get(tag);
		if (groupBag != null) {
			return groupBag;
		}
		throw new NoSuchTagException(tag);
	}

	private final class SetterDispatcher implements TagValueConsumer {
		
		@Override
		public void acceptString(StringTag tag, CharSequence value) {
			setString(tag, value.toString());
		}
		
		@Override
		public void acceptInt(IntTag tag, int value) {
			setInt(tag, value);
		}
		
		@Override
		public void acceptLong(LongTag tag, long value) {
			setLong(tag, value);
		}
		
		@Override
		public void acceptDouble(DoubleTag tag, double value) {
			setDouble(tag, value);
		}
		
		@Override
		public void acceptDecimal(DecimalTag tag, long value) {
			setDecimalUnscaled(tag, value);
		}
		
		@Override
		public void acceptChar(CharTag tag, char value) {
			setChar(tag, value);
		}
		
		@Override
		public void acceptBoolean(BooleanTag tag, boolean value) {
			setBoolean(tag, value);
		}
		
		@Override
		public void acceptGroup(GroupTag tag, int groupSize) {
			setGroup(tag, Collections.emptyList());
		}
		
		@Override
		public <T> void acceptObject(ObjectTag<T> tag, T value) {
			setObject(tag, value);
		}
		
		@Override
		public void acceptOther(FixTag tag, CharSequence value) {
			try {
				invalidValue(tag, new InvalidValueException(tag, "Unsupported 'other' type"));
			} catch (InvalidValueException e) {
				//should not get here
				throw new AssertionError(e);
			}
		}
		
		public void invalidValue(FixTag tag, InvalidValueException exception) throws InvalidValueException {
			validity.invalidValue(tag, exception);
		}
	};
}
