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
package org.fix4j.engine.stream;

import java.util.function.IntPredicate;

import org.fix4j.engine.exception.InvalidValueException;
import org.fix4j.engine.tag.FixTag;

/**
 * A tag stream is a sequential stream of tag/value pairs. The tag/value pairs can only be consumed in the given
 * sequential order. Tags can optionally be skipped which reduces the parse effort for the value.
 */
public interface TagStream {
	/**
	 * If a next tag exists, passes the tag to the given condition. If the condition predicate returns true, the tag
	 * value is parsed and passed to the specified consumer. The method returns {@code true} in whether the value was
	 * consumed or not.
	 * <p>
	 * If no next tag exists, the method returns {@code false}.
	 *
	 * @param condition
	 *            The condition for invocation of the consumer with the tag value
	 * @param consumer
	 *            The consumer for the tag/value if found and if {@code condition} evaluates to true
	 * @return {@code false} if no remaining tag existed upon entry to this method, else {@code true}.
	 * @throws InvalidValueException
	 *             if an invalid value was found and the exception was re-thrown when passed to
	 *             {@link TagValueConsumer#invalidValue(FixTag, InvalidValueException)}
	 * @throws NullPointerException
	 *             if any of the arguments is null
	 */
	boolean tryNextMatchingTag(IntPredicate condition, TagValueConsumer consumer) throws InvalidValueException;

	/**
	 * If a next tag exists, passes the tag with the associated value to the given consumer, returning {@code true};
	 * else returns {@code false}.
	 *
	 * @param consumer
	 *            The consumer for the tag/value if found
	 * @return {@code false} if no remaining tag existed upon entry to this method, else {@code true}.
	 * @throws InvalidValueException
	 *             if an invalid value was found and the exception was re-thrown when passed to
	 *             {@link TagValueConsumer#invalidValue(FixTag, InvalidValueException)}
	 * @throws NullPointerException
	 *             if the specified consumer is null
	 */
	default boolean tryNextTag(TagValueConsumer consumer) throws InvalidValueException {
		return tryNextMatchingTag((tag) -> true, consumer);
	}

	/**
	 * For each remaining tag, passes the tag with the associated value to the given consumer, sequentially until all
	 * tags have been processed or the consumer throws an exception.
	 * <p>
	 * The default implementation repeatedly invokes {@link #tryNextTag} until it returns {@code false}.
	 *
	 * @param consumer
	 *            The consumer for the tag/value pairs found
	 * @throws InvalidValueException
	 *             if an invalid value was found and the exception was re-thrown when passed to
	 *             {@link TagValueConsumer#invalidValue(FixTag, InvalidValueException)}
	 * @throws NullPointerException
	 *             if the specified consumer is null
	 */
	default void forEachRemainingTag(TagValueConsumer consumer) throws InvalidValueException {
		do {
		} while (tryNextTag(consumer));
	}

	/**
	 * For each remaining tag, passes the tag to the given condition. If the condition predicate returns true, the tag
	 * value is parsed and passed to the specified consumer. This is repeated sequentially until all tags have been
	 * processed or the consumer throws an exception.
	 * <p>
	 * The default implementation repeatedly invokes {@link #tryNextMatchingTag(IntPredicate, TagValueConsumer)} until
	 * it returns {@code false}.
	 *
	 * @param condition
	 *            The condition for invocation of the consumer with the tag value
	 * @param consumer
	 *            The consumer for the tag/value pairs found for which {@code condition} evaluates to true
	 * @throws InvalidValueException
	 *             if an invalid value was found and the exception was re-thrown when passed to
	 *             {@link TagValueConsumer#invalidValue(FixTag, InvalidValueException)}
	 * @throws NullPointerException
	 *             if any of the arguments is null
	 */
	default void forEachRemainingMatchingTag(IntPredicate condition, TagValueConsumer consumer) throws InvalidValueException {
		do {
		} while (tryNextMatchingTag(condition, consumer));
	}
}
