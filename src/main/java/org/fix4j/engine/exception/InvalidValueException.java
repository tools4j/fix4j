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
package org.fix4j.engine.exception;

import java.util.Objects;

import org.fix4j.engine.tag.FixTag;

public class InvalidValueException extends Fix4jException {
	
	private static final long serialVersionUID = 1L;

	private final FixTag fixTag;
	private final String value;
	
	public InvalidValueException(final FixTag fixTag, final CharSequence seq, int start, int end) {
		super(new StringBuilder(40)
				.append("Invalid value for tag ")
				.append(fixTag)
				.append(": ")
				.append(seq, start, end).toString());
		this.fixTag = Objects.requireNonNull(fixTag, "fixTag is null");
		this.value = seq.subSequence(start, end).toString();
	}
	public InvalidValueException(final FixTag fixTag, final CharSequence seq, int start, int end, Throwable cause) {
		this(fixTag, seq, start, end);
		initCause(cause);
	}
	public InvalidValueException(final FixTag fixTag, final String value) {
		this(fixTag, value, 0, value.length());
	}
	public InvalidValueException(final FixTag fixTag, final String value, Throwable cause) {
		this(fixTag, value, 0, value.length(), cause);
	}
	
	public FixTag getFixTag() {
		return fixTag;
	}
	public String getValue() {
		return value;
	}
}
