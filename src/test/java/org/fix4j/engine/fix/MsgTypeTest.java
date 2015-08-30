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

import org.junit.After;
import org.junit.Test;

/**
 * Unit test for {@link MsgType}.
 */
public class MsgTypeTest {
	
	@After
	public void afterEach() {
		CustomMsgType.unregisterAll();
	}

	@Test
	public void shouldParse() {
		for (final MsgType fixMsgType : FixMsgType.values()) {
			final MsgType parsed = MsgType.parse(fixMsgType.get());
			assertSame("should be same constant", fixMsgType, parsed);
		}
	}
	@Test
	public void shouldParseCustomMsgType() {
		final MsgType customType1 = CustomMsgType.register("CustomMsgType1", "U1");
		final MsgType customType2 = CustomMsgType.register("CustomMsgType2", "U2");
		final MsgType parsed1 = MsgType.parse("U1");
		final MsgType parsed2 = MsgType.parse("U2");
		assertSame("should be same constant", customType1, parsed1);
		assertSame("should be same constant", customType2, parsed2);
	}
	@Test(expected = IllegalArgumentException.class)
	public void shouldRejectCustomMsgTypeWithDuplicateName() {
		CustomMsgType.register("CustomMsgType", "U1");
		CustomMsgType.register("CustomMsgType", "U2");
	}
	@Test(expected = IllegalArgumentException.class)
	public void shouldRejectCustomMsgTypeWithDuplicateTagValue() {
		CustomMsgType.register("CustomMsgType1", "U1");
		CustomMsgType.register("CustomMsgType2", "U1");
	}
	@Test(expected = IllegalArgumentException.class)
	public void shouldRejectCustomMsgTypeWithStandardName() {
		CustomMsgType.register("NewOrderSingle", "U1");
	}
	@Test(expected = IllegalArgumentException.class)
	public void shouldRejectCustomMsgTypeWithStandardTagValue() {
		CustomMsgType.register("CustomMsgType1", "A");
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
