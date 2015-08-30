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
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Predicate;

/**
 * User-defined custom message types.
 */
public final class CustomMsgType implements MsgType {

	private static final Queue<CustomMsgType> REGISTRY = new ConcurrentLinkedQueue<>();

	private final String name;
	private final String tagValue;
	private final int ordinal;

	private CustomMsgType(final String name, final String tagValue, final int ordinal) {
		this.name = Objects.requireNonNull(name, "name is null");
		this.tagValue = Objects.requireNonNull(tagValue, "tagValue is null");
		this.ordinal = ordinal;
	}

	public static final synchronized MsgType register(final String name, final String tagValue) {
		checkNonStandard(name, tagValue);
		final Predicate<CustomMsgType> matchNameOrType = (m) -> m.name.equals(name) || m.tagValue.equals(tagValue);
		final CustomMsgType msgType; 
		final CustomMsgType existing;
		synchronized(REGISTRY) {
			msgType = new CustomMsgType(name, tagValue, FixMsgType.VALUES.size() + REGISTRY.size());
			if (REGISTRY.stream().noneMatch(matchNameOrType)) {
				REGISTRY.add(msgType);
				return msgType;
			}
			existing = REGISTRY.stream().filter(matchNameOrType).findAny().get();
		}
		throw new IllegalArgumentException("Conflicting custom message type definitions: " + existing + ", " + msgType);
	}

	private static void checkNonStandard(final String name, final String tagValue) {
		MsgType standard = FixMsgType.parse(tagValue);
		if (standard != null) {
			throw new IllegalArgumentException("Tag value '" + tagValue
					+ "' of custom message type definition conflicts with standard FIX message type: " + standard);
		}
		try {
			standard = FixMsgType.valueOf(name);
		} catch (IllegalArgumentException e) {
			// as expected
			return;
		}
		throw new IllegalArgumentException("Name '" + name
				+ "' of custom message type definition conflicts with standard FIX message type: " + standard);
	}

	public static final void unregisterAll() {
		REGISTRY.clear();
	}

	public static MsgType parse(final CharSequence tagValue) {
		for (final MsgType type : REGISTRY) {
			if (type.get().contentEquals(tagValue)) {
				return type;
			}
		}
		return null;
	}

	@Override
	public boolean isCustom() {
		return true;
	}

	@Override
	public String name() {
		return name;
	}

	@Override
	public int getTag() {
		return MsgType;
	}

	@Override
	public String get() {
		return tagValue;
	}
	
	@Override
	public int ordinal() {
		return ordinal;
	}

	@Override
	public String toString() {
		return "CustomMsgType{name=" + name + ",tagValue=" + tagValue + "}";
	}

}
