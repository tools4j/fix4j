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
package org.fix4j.engine.tag;

import java.io.IOException;
import java.util.Objects;

import org.decimal4j.api.DecimalArithmetic;
import org.decimal4j.scale.Scale0f;

public class EnumTag<T extends Enum<T>> implements ObjectTag<T> {
	
	private static final DecimalArithmetic INT_ARITH = Scale0f.INSTANCE.getRoundingUnnecessaryArithmetic();

	private final String name;
	private final int tag;
	private final Class<T> enumType;
	private final T[] universe;
	
	public EnumTag(final String name, final int tag, final Class<T> enumType) {
		if (tag <= 0) throw new IllegalArgumentException("invalid tag: " + tag);
		this.name = Objects.requireNonNull(name, "name is null");
		this.tag = tag;
		this.enumType = Objects.requireNonNull(enumType, "enumType is null");
		this.universe = enumType.getEnumConstants();
	}
	
	@Override
	public String name() {
		return name;
	}
	
	@Override
	public int tag() {
		return tag;
	}

	@Override
	public Class<T> valueType() {
		return enumType;
	}
	
	@Override
	public T convertFrom(CharSequence value, int start, int end) {
		final long ordinal = INT_ARITH.parse(value, start, end);
		if (ordinal >= 0 & ordinal < universe.length) {
			return universe[(int)ordinal];
		}
		throw new IllegalArgumentException("invalid ordinal value for enum tag " + tag() + " for enum " + enumType.getName());
	}
	
	@Override
	public void convertTo(T value, Appendable destination) {
		try {
			INT_ARITH.toString(value.ordinal(), destination);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
