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
package org.fix4j.engine.fix;

import java.util.Objects;

/**
 * Constants for the FIX version.
 */
public enum FixVersion {
	FIX_4_0("FIX.4.0"), //
	FIX_4_1("FIX.4.1"), //
	FIX_4_2("FIX.4.2"), //
	FIX_4_3("FIX.4.3"), //
	FIX_4_4("FIX.4.4"), //
	FIX_5_0("FIXT.1.1");//FIXME 5.0 has no begin-string

	private final String beginString;

	private static final FixVersion[] VALUES = values();

	private FixVersion(final String beginString) {
		this.beginString = Objects.requireNonNull(beginString, "beginString is null");
	}

	/**
	 * Returns the begin string for this fix version, such as "FIX.4.4".
	 * 
	 * @return the begin string tag content for this fix version
	 */
	public String getBeginString() {
		return beginString;
	}

	/**
	 * Returns true if the given argument matches the begin-string of this {@code FixVersion}.
	 * 
	 * @param beginString
	 *            the begin-string to compare, for instance "FIX.4.4"
	 * @return true if it matches the begin-string of this {@code FixVersion} constant
	 */
	public final boolean matchesBeginString(final CharSequence beginString) {
		final String toMatch = this.beginString;
		final int len = toMatch.length();
		if (beginString.length() != len) {
			return false;
		}
		// compare from back for increased performance of non-match
		for (int i = len - 1; i >= 0; i--) {
			if (beginString.charAt(i) != toMatch.charAt(i)) {
				return false;
			}
		}
		return true;
	}

	public static FixVersion parse(final String beginString) {
		for (final FixVersion value : VALUES) {
			if (value.matchesBeginString(beginString)) {
				return value;
			}
		}
		throw new IllegalArgumentException("Not a valid begin-string: " + beginString);
	}

}
