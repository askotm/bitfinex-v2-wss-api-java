/*******************************************************************************
 *
 *    Copyright (C) 2015-2018 Jan Kristof Nidzwetzki
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *
 *******************************************************************************/
package com.github.jnidzwetzki.bitfinex.v2.test;

import org.junit.Assert;
import org.junit.Test;

import com.github.jnidzwetzki.bitfinex.v2.entity.BitfinexOrderType;

public class BitfinexOrderTypeTest {

    @Test
    public void allProtocolStringsRoundTrip() {
        for (BitfinexOrderType type : BitfinexOrderType.values()) {
            Assert.assertEquals(type, BitfinexOrderType.fromBifinexString(type.getBifinexString()));
        }
    }

    // M2 regression: IOC / EXCHANGE IOC must map to their dedicated enum constants
    @Test
    public void iocMapsToCorrectEnum() {
        Assert.assertEquals(BitfinexOrderType.IOC, BitfinexOrderType.fromBifinexString("IOC"));
        Assert.assertEquals(BitfinexOrderType.EXCHANGE_IOC, BitfinexOrderType.fromBifinexString("EXCHANGE IOC"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void unknownStringThrows() {
        BitfinexOrderType.fromBifinexString("UNKNOWN");
    }
}
