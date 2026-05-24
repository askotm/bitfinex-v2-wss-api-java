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
package com.github.jnidzwetzki.bitfinex.v2.entity;

import java.math.BigDecimal;

/**
 * Funding ticker (fXXX) — 13-field layout from Bitfinex WS v2.
 * Extends BitfinexTick so existing onTickEvent consumers receive it transparently;
 * cast to this type to access the three funding-only fields.
 */
public class BitfinexFundingTick extends BitfinexTick {

    /** Flash Return Rate */
    private final BigDecimal frr;

    /** Funding period (days) at best bid */
    private final BigDecimal bidPeriod;

    /** Funding period (days) at best ask */
    private final BigDecimal askPeriod;

    public BitfinexFundingTick(final BigDecimal frr,
                               final BigDecimal bid, final BigDecimal bidPeriod, final BigDecimal bidSize,
                               final BigDecimal ask, final BigDecimal askPeriod, final BigDecimal askSize,
                               final BigDecimal dailyChange, final BigDecimal dailyChangePerc,
                               final BigDecimal lastPrice, final BigDecimal volume,
                               final BigDecimal high, final BigDecimal low) {
        super(bid, bidSize, ask, askSize, dailyChange, dailyChangePerc, lastPrice, volume, high, low);
        this.frr = frr;
        this.bidPeriod = bidPeriod;
        this.askPeriod = askPeriod;
    }

    public BigDecimal getFrr() {
        return frr;
    }

    public BigDecimal getBidPeriod() {
        return bidPeriod;
    }

    public BigDecimal getAskPeriod() {
        return askPeriod;
    }

    @Override
    public String toString() {
        return "BitfinexFundingTick [frr=" + frr + ", bid=" + getBid() + ", bidPeriod=" + bidPeriod
                + ", bidSize=" + getBidSize() + ", ask=" + getAsk() + ", askPeriod=" + askPeriod
                + ", askSize=" + getAskSize() + ", dailyChange=" + getDailyChange()
                + ", dailyChangePerc=" + getDailyChangePerc() + ", lastPrice=" + getLastPrice()
                + ", volume=" + getVolume() + ", high=" + getHigh() + ", low=" + getLow() + "]";
    }
}
