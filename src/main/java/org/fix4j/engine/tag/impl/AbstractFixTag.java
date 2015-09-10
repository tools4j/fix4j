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

import org.fix4j.engine.tag.FixTag;

abstract public class AbstractFixTag implements FixTag {
	
	private final String name;
	private final int tag;

	public AbstractFixTag(final int tag) {
		this.name = getSimpleTagName() + "(" + tag + ")";
		this.tag = validateTag(tag);
	}
	public AbstractFixTag(final String name, final int tag) {
		this.name = Objects.requireNonNull(name, "name is null");
		this.tag = validateTag(tag);
	}
	private String getSimpleTagName() {
		final String name = getClass().getSimpleName();
		return name.endsWith("Tag") ? name.substring(0, name.length() - 3) : name;
	}
	
	private static int validateTag(int tag) {
		if (tag > 0) {
			return tag;
		}
		throw new IllegalArgumentException("invalid tag: " + tag);
	}

	@Override
	public final int tag() {
		return tag;
	}
	
	@Override
	public int hashCode() {
		return tag;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (obj instanceof FixTag) {
			return tag == ((FixTag)obj).tag();
		}
		return false;
	}

	@Override
	public String toString() {
		return name;
	}
}
