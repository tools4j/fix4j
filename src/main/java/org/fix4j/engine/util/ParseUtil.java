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
package org.fix4j.engine.util;

import org.decimal4j.api.DecimalArithmetic;
import org.decimal4j.scale.Scale0f;
import org.fix4j.engine.exception.InvalidValueException;
import org.fix4j.engine.tag.BooleanTag;
import org.fix4j.engine.tag.CharTag;
import org.fix4j.engine.tag.DecimalTag;
import org.fix4j.engine.tag.DoubleTag;
import org.fix4j.engine.tag.IntTag;
import org.fix4j.engine.tag.LongTag;

public class ParseUtil {
	
	public static final DecimalArithmetic LONG_ARITHMETIC = Scale0f.INSTANCE.getRoundingUnnecessaryArithmetic();
	
	public static int parseInt(CharSequence seq, int start, int end) {
		final long value = parseLong(seq, start, end);
		if (Integer.MIN_VALUE <= value & value <= Integer.MAX_VALUE) {
			return (int)value;
		}
		throw new ArithmeticException("Overflow: value " + value + " is out of the integer range");
	}
	public static long parseLong(CharSequence seq, int start, int end) {
		return LONG_ARITHMETIC.parse(seq, start, end);
	}

	public static int parseInt(IntTag tag, CharSequence seq, int start, int end) throws InvalidValueException {
		try {
			return parseInt(seq, start, end);
		} catch (Exception e) {
			throw new InvalidValueException(tag, seq, start, end, e);
		}
	}
	public static long parseLong(LongTag tag, CharSequence seq, int start, int end) throws InvalidValueException {
		try {
			return parseLong(seq, start, end);
		} catch (Exception e) {
			throw new InvalidValueException(tag, seq, start, end, e);
		}
	}
	
	public static double parseDouble(DoubleTag tag, CharSequence seq, int start, int end) throws InvalidValueException {
		try {
			final String s = seq.subSequence(start, end).toString();//FIXME make this garbage free
			return Double.parseDouble(s);
		} catch (Exception e) {
			throw new InvalidValueException(tag, seq, start, end, e);
		}
	}

	public static long parseDecimal(DecimalTag tag, CharSequence seq, int start, int end) throws InvalidValueException {
		try {
			return tag.getArithmetic().parse(seq, start, end);
		} catch (Exception e) {
			throw new InvalidValueException(tag, seq, start, end, e);
		}
	}
	
	public static boolean parseBoolean(BooleanTag tag, CharSequence seq, int start, int end) throws InvalidValueException {
		if (end - start == 1) {
			final char ch = seq.charAt(start);
			if (ch == 'Y') return true;
			if (ch == 'N') return false;
		}
		throw new InvalidValueException(tag, seq, start, end);
	}
	
	public static char parseChar(CharTag tag, CharSequence seq, int start, int end) throws InvalidValueException {
		if (end - start == 1) {
			return seq.charAt(start);
		}
		throw new InvalidValueException(tag, seq, start, end);
	}
	
	//no instances
	private ParseUtil() {
		super();
	}
}
