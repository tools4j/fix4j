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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * FixMsgType constants for all fix versions.
 */
public enum FixMsgType implements MsgType, Supplier<String> {
	Heartbeat("0"),
	TestRequest("1"),
	ResendRequest("2"),
	Reject("3"),
	SequenceReset("4"),
	Logout("5"),
	IOI("6"),
	Advertisement("7"),
	ExecutionReport("8"),
	OrderCancelReject("9"),
	Logon("A"),
	DerivativeSecurityList("AA"),
	NewOrderMultileg("AB"),
	MultilegOrderCancelReplace("AC"),
	TradeCaptureReportRequest("AD"),
	TradeCaptureReport("AE"),
	OrderMassStatusRequest("AF"),
	QuoteRequestReject("AG"),
	RFQRequest("AH"),
	QuoteStatusReport("AI"),
	QuoteResponse("AJ"),
	Confirmation("AK"),
	PositionMaintenanceRequest("AL"),
	PositionMaintenanceReport("AM"),
	RequestForPositions("AN"),
	RequestForPositionsAck("AO"),
	PositionReport("AP"),
	TradeCaptureReportRequestAck("AQ"),
	TradeCaptureReportAck("AR"),
	AllocationReport("AS"),
	AllocationReportAck("AT"),
	Confirmation_Ack("AU"),
	SettlementInstructionRequest("AV"),
	AssignmentReport("AW"),
	CollateralRequest("AX"),
	CollateralAssignment("AY"),
	CollateralResponse("AZ"),
	News("B"),
	CollateralReport("BA"),
	CollateralInquiry("BB"),
	NetworkCounterpartySystemStatusRequest("BC"),
	NetworkCounterpartySystemStatusResponse("BD"),
	UserRequest("BE"),
	UserResponse("BF"),
	CollateralInquiryAck("BG"),
	ConfirmationRequest("BH"),
	TradingSessionListRequest("BI"),
	TradingSessionList("BJ"),
	SecurityListUpdateReport("BK"),
	AdjustedPositionReport("BL"),
	AllocationInstructionAlert("BM"),
	ExecutionAcknowledgement("BN"),
	ContraryIntentionReport("BO"),
	SecurityDefinitionUpdateReport("BP"),
	SettlementObligationReport("BQ"),
	DerivativeSecurityListUpdateReport("BR"),
	TradingSessionListUpdateReport("BS"),
	MarketDefinitionRequest("BT"),
	MarketDefinition("BU"),
	MarketDefinitionUpdateReport("BV"),
	ApplicationMessageRequest("BW"),
	ApplicationMessageRequestAck("BX"),
	ApplicationMessageReport("BY"),
	OrderMassActionReport("BZ"),
	Email("C"),
	OrderMassActionRequest("CA"),
	UserNotification("CB"),
	StreamAssignmentRequest("CC"),
	StreamAssignmentReport("CD"),
	StreamAssignmentReportACK("CE"),
	PartyDetailsListRequest("CF"),
	PartyDetailsListReport("CG"),
	NewOrderSingle("D"),
	NewOrderList("E"),
	OrderCancelRequest("F"),
	OrderCancelReplaceRequest("G"),
	OrderStatusRequest("H"),
	AllocationInstruction("J"),
	ListCancelRequest("K"),
	ListExecute("L"),
	ListStatusRequest("M"),
	ListStatus("N"),
	AllocationInstructionAck("P"),
	DontKnowTradeDK("Q"),
	QuoteRequest("R"),
	Quote("S"),
	SettlementInstructions("T"),
	MarketDataRequest("V"),
	MarketDataSnapshotFullRefresh("W"),
	MarketDataIncrementalRefresh("X"),
	MarketDataRequestReject("Y"),
	QuoteCancel("Z"),
	QuoteStatusRequest("a"),
	MassQuoteAcknowledgement("b"),
	SecurityDefinitionRequest("c"),
	SecurityDefinition("d"),
	SecurityStatusRequest("e"),
	SecurityStatus("f"),
	TradingSessionStatusRequest("g"),
	TradingSessionStatus("h"),
	MassQuote("i"),
	BusinessMessageReject("j"),
	BidRequest("k"),
	BidResponse("l"),
	ListStrikePrice("m"),
	XML_non_FIX("n"),
	RegistrationInstructions("o"),
	RegistrationInstructionsResponse("p"),
	OrderMassCancelRequest("q"),
	OrderMassCancelReport("r"),
	NewOrderCross("s"),
	CrossOrderCancelReplaceRequest("t"),
	CrossOrderCancelRequest("u"),
	SecurityTypeRequest("v"),
	SecurityTypes("w"),
	SecurityListRequest("x"),
	SecurityList("y"),
	DerivativeSecurityListRequest("z");
	
	private final String tagValue;
	
	/**
	 * All standard FIX MsgType constants in an unmodifiable list. Similar to {@link #values()} but without
	 * creating new instances for every invocation.
	 */
	public static final List<FixMsgType> VALUES = Collections.unmodifiableList(Arrays.asList(values()));

	private static final FixMsgType[][] LOOKUP = initLookup();//lookup by first letter, then by second
	private static final char LOOKUP_MAX_LEADING = 'C';//Cx but no Dx
	private static final char LOOKUP_MAX_TRAILING = 'G';//CG but no CH
	
	private FixMsgType(final String tagValue) {
		this.tagValue = Objects.requireNonNull(tagValue, "tagValue is null");
	}
	
	@Override
	public boolean isCustom() {
		return false;
	}
	
	@Override
	public int tag() {
		return MsgType;
	}
	
	@Override
	public String get() {
		return tagValue;
	}
	
	public static MsgType parse(final CharSequence tagValue) {
		final int i = lookupIndex0(tagValue);
		final int j = lookupIndex1(tagValue);
		if (i != -1 & j != -1) {
			return LOOKUP[i][j];
		}
		return CustomMsgType.parse(tagValue);
	}
	
	private static FixMsgType[][] initLookup() {
		final FixMsgType[][] types = new FixMsgType['C'-'A'+2][];
		types[0] = new FixMsgType['9'-'0'+1+'Z'-'A'+1+'z'-'a'+1];//x
		for (int i = 1; i <= LOOKUP_MAX_LEADING - 'A'; i++) {
			types[i] = new FixMsgType['Z'-'A'+1];//Ax, Bx, ...
		}
		types[LOOKUP_MAX_LEADING-'A'+1] = new FixMsgType[LOOKUP_MAX_TRAILING-'A'+1];//Cx
		for (final FixMsgType value : VALUES) {
			final String msgType = value.get();
			final int i = lookupIndex0(msgType);
			final int j = lookupIndex1(msgType);
			types[i][j] = value;
		}
		return types;
	}
	private static final int lookupIndex0(final CharSequence tagValue) {
		final int len = tagValue.length();
		if (len == 1) {
			return 0;
		}
		if (len == 2) {
			final char ch = tagValue.charAt(0);
			if (ch >= 'A' & ch <= LOOKUP_MAX_LEADING) {
				return ch - 'A' + 1;
			}
		}
		return -1;
	}
	private static final int lookupIndex1(final CharSequence tagValue) {
		final int len = tagValue.length();
		if (len == 1) {
			final char ch = tagValue.charAt(0);
			if (ch >= '0' & ch <= '9') {
				return ch - '0';
			} else if (ch >= 'A' & ch <= 'Z') {
				return '9' - '0' + 1 + ch - 'A';
			} else if (ch >= 'a' & ch <= 'z') {
				return '9' - '0' + 1 + 'Z' - 'A' + 1 + ch - 'a';
			}
		} else if (len == 2) {
			final char ch = tagValue.charAt(1);
			if (ch >= 'A' & ch <= 'Z') {
				if (ch <= LOOKUP_MAX_TRAILING || tagValue.charAt(0) < LOOKUP_MAX_LEADING) {
					return ch - 'A';
				}
			}
		}
		return -1;
	}
}
