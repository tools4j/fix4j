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

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * User-defined custom message types
 * 
 * @author terz
 *
 */
public final class CustomMsgType implements MsgType {

	private static final List<CustomMsgType> REGISTRY = new CopyOnWriteArrayList<>();

	private final String name;
	private final String tagValue;

	private CustomMsgType(final String name, final String tagValue) {
		this.name = Objects.requireNonNull(name, "name is null");
		this.tagValue = Objects.requireNonNull(tagValue, "tagValue is null");
	}

	public static final MsgType register(String name, String tagValue) {
		final CustomMsgType msgType = new CustomMsgType(name, tagValue);
		checkNonStandard(msgType);
		REGISTRY.add(msgType);
		final Predicate<CustomMsgType> matchNameOrType = (m) -> m.name.equals(name) || m.tagValue.equals(tagValue);
		if (REGISTRY.stream().filter(matchNameOrType).count() > 1) {
			final List<CustomMsgType> types = REGISTRY.stream().filter(matchNameOrType).collect(Collectors.toList());
			REGISTRY.remove(msgType);
			throw new IllegalArgumentException("Conflicting custom message type definitions: " + types);
		}
		return msgType;
	}

	private static void checkNonStandard(CustomMsgType msgType) {
		MsgType standard = FixMsgType.parse(msgType.get());
		if (standard != null) {
			throw new IllegalArgumentException(
					"Tag value of custom message type definition conflicts with standard FIX message type: custom="
							+ msgType + ", standard=" + standard);
		}
		try {
			standard = FixMsgType.valueOf(msgType.name());
		} catch (IllegalArgumentException e) {
			// as expected
			return;
		}
		throw new IllegalArgumentException(
				"Name of custom message type definition conflicts with standard FIX message type: custom=" + msgType
						+ ", standard=" + standard);
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
	public String toString() {
		return "CustomMsgType{name=" + name + ",tagValue=" + tagValue + "}";
	}

}
