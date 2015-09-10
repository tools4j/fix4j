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

import java.util.Objects;

import org.decimal4j.api.DecimalArithmetic;
import org.fix4j.engine.tag.DecimalTag;

public class BasicDecimalTag extends AbstractFixTag implements DecimalTag {

	private final DecimalArithmetic arithmetic;

	public BasicDecimalTag(final int tag, final DecimalArithmetic arithmetic) {
		super(tag);
		this.arithmetic = Objects.requireNonNull(arithmetic, "arithmetic is null");
	}
	
	public BasicDecimalTag(final String name, final int tag, final DecimalArithmetic arithmetic) {
		super(name, tag);
		this.arithmetic = Objects.requireNonNull(arithmetic, "arithmetic is null");
	}
	
	@Override
	public final DecimalArithmetic getArithmetic() {
		return arithmetic;
	}

}
