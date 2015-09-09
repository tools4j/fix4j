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

import java.util.Objects;
import java.util.function.IntPredicate;

import org.decimal4j.api.DecimalArithmetic;
import org.decimal4j.scale.Scale0f;
import org.fix4j.engine.tag.FixTag;
import org.fix4j.engine.tag.TagLib;
import org.fix4j.engine.tag.TagValueConsumer;

public class CharSequenceTagStream implements TagStream {
	
	private static final DecimalArithmetic INT_ARITH = Scale0f.INSTANCE.getRoundingUnnecessaryArithmetic();
	private static final TagLib TAGLIB = new TagLib();
	
	private final CharSequence seq;
	private final Token token = new Token();
	
	public CharSequenceTagStream(final CharSequence seq) {
		this.seq = Objects.requireNonNull(seq, "seq is null");
	}
	
	@Override
	public boolean tryNextTagConditional(IntPredicate condition, TagValueConsumer consumer) {
		final int tag = readNextTag();
		if (tag > 0) {
			readNextValue();
			if (condition.test(tag)) {
				final FixTag fixTag = TAGLIB.get(tag);
				fixTag.dispatch(token, consumer);
			}
			//skip '\0'
			token.end++;
			return true;
		}
		return false;
	}
	
	private final int readNextTag() {
		token.start = token.end;
		if (token.hasNextChar()) {
			char ch = token.readNextChar();
			while (ch != '=' && isDigit(ch) && token.hasNextChar()) {
				ch = token.readNextChar();
			}
			if (ch != '=') {
				throw new IllegalStateException("Expected '=' but found " + (isDigit(ch) ? "eof" : ("'" + ch + "'")));
			}
			return (int)INT_ARITH.parse(seq, token.start, token.end);
		}
		return 0;
	}
	
	private final void readNextValue() {
		token.start = token.end;
		if (token.hasNextChar()) {
			char ch = token.readNextChar();
			while (ch != '\0' && token.hasNextChar()) {
				ch = token.readNextChar();
			}
			if (ch == '\0') {
				token.end--;
			}
		}
	}

	private final boolean isDigit(char ch) {
		return '0' <= ch & ch <= '9';
	}
	
	private class Token implements CharSequence {
		private int start = 0;
		private int end = 0;
		@Override
		public final CharSequence subSequence(int start, int end) {
			return toStringBuilder().subSequence(start, end);
		}
		
		@Override
		public final int length() {
			return end - start;
		}
		
		@Override
		public final char charAt(int index) {
			if (index < 0 | index >= length()) {
				throw new IndexOutOfBoundsException(index + " is not in [0," + length() + "]");
			}
			return seq.charAt(start + index);
		}
		public final String toString() {
			return toStringBuilder().toString();
		}
		public final StringBuilder toStringBuilder() {
			return new StringBuilder(this);
		}
		private final boolean hasNextChar() {
			return end < seq.length();
		}
		private char readNextChar() {
			final char ch = charAt(end);
			end++;
			return ch;
		}
	}

}
