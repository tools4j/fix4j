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

import static org.junit.Assert.assertSame;

import org.junit.Test;

/**
 * Unit test for {@link MsgType}.
 */
public class MsgTypeTest {

	@Test
	public void shouldParse() {
		for (final MsgType msgType : MsgType.values()) {
			final MsgType parsed = MsgType.parse(msgType.getMsgType());
			assertSame("should be same constant", msgType, parsed);
		}
	}
	@Test(expected = NullPointerException.class)
	public void parseShouldThrowExceptionForNullMsgType() {
		MsgType.parse(null);
	}
	@Test(expected = IllegalArgumentException.class)
	public void parseShouldThrowExceptionForEmptyMsgType() {
		MsgType.parse("");
	}
	@Test(expected = IllegalArgumentException.class)
	public void parseShouldThrowExceptionForInvalidMsgType() {
		MsgType.parse("*");
	}
	@Test(expected = IllegalArgumentException.class)
	public void parseShouldThrowExceptionForInvalidMsgTypeA() {
		MsgType.parse("A*");
	}
	@Test(expected = IllegalArgumentException.class)
	public void parseShouldThrowExceptionForInvalidMsgTypeB() {
		MsgType.parse("B*");
	}
	@Test(expected = IllegalArgumentException.class)
	public void parseShouldThrowExceptionForInvalidMsgTypeC() {
		MsgType.parse("CH");
	}
	@Test(expected = IllegalArgumentException.class)
	public void parseShouldThrowExceptionForInvalidMsgTypeD() {
		MsgType.parse("DA");
	}
	@Test(expected = IllegalArgumentException.class)
	public void parseShouldThrowExceptionForInvalidMsgTypeA3() {
		MsgType.parse("ABC");
	}
}
