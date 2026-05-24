package com.github.jnidzwetzki.bitfinex.v2.test;

import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import com.github.jnidzwetzki.bitfinex.v2.BitfinexConnectionFeature;

public class BitfinexConnectionFeatureTest {

	@Test
	public void testAllFlagsAreUnique() {
		final Set<Integer> seen = new HashSet<>();
		for (final BitfinexConnectionFeature f : BitfinexConnectionFeature.values()) {
			Assert.assertTrue("duplicate flag: " + f, seen.add(f.getFeatureFlag()));
		}
	}

	@Test
	public void testTimestampFlag() {
		Assert.assertEquals(32768, BitfinexConnectionFeature.TIMESTAMP.getFeatureFlag());
	}

	@Test
	public void testFlagsArePowersOfTwo() {
		for (final BitfinexConnectionFeature f : BitfinexConnectionFeature.values()) {
			final int v = f.getFeatureFlag();
			Assert.assertTrue("not a power of two: " + f, v > 0 && (v & (v - 1)) == 0);
		}
	}
}
