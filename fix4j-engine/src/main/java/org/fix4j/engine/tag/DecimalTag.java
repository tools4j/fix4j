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
package org.fix4j.engine.tag;

import java.io.IOException;

import org.decimal4j.api.DecimalArithmetic;
import org.fix4j.engine.exception.InvalidValueException;
import org.fix4j.engine.stream.TagValueConsumer;
import org.fix4j.engine.util.ParseUtil;

public interface DecimalTag extends FixTag {
	
	/**
	 * Returns the arithmetic defining the scale (aka precision) of the decimal value.
	 * @return the arithmetic with scale, rounding and overflow mode for the parsing
	 */
	DecimalArithmetic getArithmetic();

	/**
	 * Returns the precision for tag values, for instance 2 for 2 decimal places.
	 * @return the precision after the decimal point
	 */
	default int getPrecision() {
		return getArithmetic().getScale();
	}

	@Override
	default void dispatch(CharSequence value, TagValueConsumer consumer) throws InvalidValueException {
		consumer.acceptDecimal(this, convertFrom(value, 0, value.length()));
	}
	default long convertFrom(CharSequence value, int start, int end) throws InvalidValueException {
		return ParseUtil.parseDecimal(this, value, start, end);
	}
	default void convertTo(long value, Appendable destination) throws IOException {
		getArithmetic().toString(value, destination);
	}
	default String convertToString(long value) {
		return getArithmetic().toString(value);
	}
}