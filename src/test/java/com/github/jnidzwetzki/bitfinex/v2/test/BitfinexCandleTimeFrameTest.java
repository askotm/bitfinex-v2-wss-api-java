package com.github.jnidzwetzki.bitfinex.v2.test;

import org.junit.Assert;
import org.junit.Test;

import com.github.jnidzwetzki.bitfinex.v2.entity.BitfinexCandleTimeFrame;

public class BitfinexCandleTimeFrameTest {

	@Test
	public void testAllFramesRoundTripViaSymbolString() {
		for (final BitfinexCandleTimeFrame frame : BitfinexCandleTimeFrame.values()) {
			final String s = frame.getBitfinexString();
			Assert.assertEquals(frame, BitfinexCandleTimeFrame.fromSymbolString(s));
		}
	}

	@Test
	public void testNewFrames() {
		Assert.assertEquals(BitfinexCandleTimeFrame.HOUR_2, BitfinexCandleTimeFrame.fromSymbolString("2h"));
		Assert.assertEquals(BitfinexCandleTimeFrame.HOUR_4, BitfinexCandleTimeFrame.fromSymbolString("4h"));
		Assert.assertEquals(BitfinexCandleTimeFrame.WEEK_1, BitfinexCandleTimeFrame.fromSymbolString("1W"));
	}

	@Test
	public void testMilliseconds() {
		Assert.assertEquals(2 * 3600 * 1000L, BitfinexCandleTimeFrame.HOUR_2.getMilliSeconds());
		Assert.assertEquals(4 * 3600 * 1000L, BitfinexCandleTimeFrame.HOUR_4.getMilliSeconds());
		Assert.assertEquals(7 * 24 * 3600 * 1000L, BitfinexCandleTimeFrame.WEEK_1.getMilliSeconds());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testUnknownSymbolThrows() {
		BitfinexCandleTimeFrame.fromSymbolString("99x");
	}
}
