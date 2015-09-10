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
package org.fix4j.engine.tag.impl;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Objects;

import org.fix4j.engine.exception.InvalidValueException;

public class CharEncodedEnumTag<T extends Enum<T>> extends AbstractEnumTag<T> {

	private final CharEncoder<T> enumToCharEncoder;
	private final T[] universe;

	public static interface CharEncoder<T> {
		char apply(T value);
	}

	public CharEncodedEnumTag(final int tag, final Class<T> enumType, final CharEncoder<T> enumToCharEncoder) {
		super(tag, enumType);
		this.enumToCharEncoder = Objects.requireNonNull(enumToCharEncoder, "enumToCharEncoder is null");
		this.universe = initUniverse(enumType, enumToCharEncoder);
	}

	public CharEncodedEnumTag(final String name, final int tag, final Class<T> enumType, final CharEncoder<T> enumToCharEncoder) {
		super(name, tag, enumType);
		this.enumToCharEncoder = Objects.requireNonNull(enumToCharEncoder, "enumToCharEncoder is null");
		this.universe = initUniverse(enumType, enumToCharEncoder);
	}

	private static <T extends Enum<T>> T[] initUniverse(final Class<T> enumType, final CharEncoder<T> enumToCharEncoder) {
		final T[] universe = enumType.getEnumConstants();// TODO avoid this garbage? how?
		// find max first
		int max = 0;
		for (final T value : universe) {
			final char code = enumToCharEncoder.apply(value);
			if (code > 127) {
				throw new IllegalArgumentException("invalid char encoding, must be ASCII in [0, 127] but was: " + code);
			}
			max = Math.max(max, code);
		}
		// create universe array now
		@SuppressWarnings("unchecked") // safe cast
		final T[] mappedUniverse = (T[]) Array.newInstance(enumType, max);
		for (final T value : universe) {
			final char code = enumToCharEncoder.apply(value);
			if (mappedUniverse[code] != null) {
				throw new IllegalArgumentException("enumCharEncoder returns same code '" + code
						+ "' for different constants: " + mappedUniverse[code] + ", " + value);
			}
			mappedUniverse[code] = value;
		}
		return mappedUniverse;
	}

	@Override
	public T convertFrom(CharSequence value, int start, int end) throws InvalidValueException {
		if (end - start == 1) {
			final char code = value.charAt(start);
			if (code < universe.length) {
				final T enumValue = universe[code];
				if (enumValue != null) {
					return enumValue;
				}
			}
		}
		throw new InvalidValueException(this, value, start, end);
	}

	@Override
	public void convertTo(T value, Appendable destination) throws IOException {
		final char code = enumToCharEncoder.apply(value);
		destination.append(code);
	}
}
