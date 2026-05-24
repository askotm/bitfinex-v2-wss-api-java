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
package com.github.jnidzwetzki.bitfinex.v2.callback.channel;

import java.util.function.BiConsumer;

import org.json.JSONArray;

import com.github.jnidzwetzki.bitfinex.v2.entity.BitfinexFundingTick;
import com.github.jnidzwetzki.bitfinex.v2.entity.BitfinexTick;
import com.github.jnidzwetzki.bitfinex.v2.entity.currency.BitfinexFundingCurrency;
import com.github.jnidzwetzki.bitfinex.v2.exception.BitfinexClientException;
import com.github.jnidzwetzki.bitfinex.v2.symbol.BitfinexStreamSymbol;
import com.github.jnidzwetzki.bitfinex.v2.symbol.BitfinexTickerSymbol;

public class TickHandler implements ChannelCallbackHandler {

    private final int channelId;
    private final BitfinexTickerSymbol symbol;

    private BiConsumer<BitfinexTickerSymbol, BitfinexTick> tickConsumer = (s, t) -> {};

    public TickHandler(int channelId, final BitfinexTickerSymbol symbol) {
        this.channelId = channelId;
        this.symbol = symbol;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void handleChannelData(final String action, final JSONArray jsonArray) throws BitfinexClientException {
        final boolean isFunding = symbol.getCurrency() instanceof BitfinexFundingCurrency;
        final BitfinexTick tick = isFunding ? parseFundingTick(jsonArray) : parseTradingTick(jsonArray);
        tickConsumer.accept(symbol, tick);
    }

    @Override
    public BitfinexStreamSymbol getSymbol() {
        return symbol;
    }

    @Override
    public int getChannelId() {
        return channelId;
    }

    // Trading layout: BID, BID_SIZE, ASK, ASK_SIZE, DAILY_CHANGE, DAILY_CHANGE_REL, LAST_PRICE, VOLUME, HIGH, LOW
    private static BitfinexTick parseTradingTick(final JSONArray a) {
        return new BitfinexTick(
                a.getBigDecimal(0), a.getBigDecimal(1), a.getBigDecimal(2), a.getBigDecimal(3),
                a.getBigDecimal(4), a.getBigDecimal(5), a.getBigDecimal(6), a.getBigDecimal(7),
                a.getBigDecimal(8), a.getBigDecimal(9));
    }

    // Funding layout: FRR, BID, BID_PERIOD, BID_SIZE, ASK, ASK_PERIOD, ASK_SIZE, DAILY_CHANGE, DAILY_CHANGE_REL, LAST_PRICE, VOLUME, HIGH, LOW
    private static BitfinexFundingTick parseFundingTick(final JSONArray a) {
        return new BitfinexFundingTick(
                a.getBigDecimal(0), a.getBigDecimal(1), a.getBigDecimal(2), a.getBigDecimal(3),
                a.getBigDecimal(4), a.getBigDecimal(5), a.getBigDecimal(6), a.getBigDecimal(7),
                a.getBigDecimal(8), a.getBigDecimal(9), a.getBigDecimal(10), a.getBigDecimal(11),
                a.getBigDecimal(12));
    }

    /**
     * bitfinex tick event consumer
     *
     * @param consumer of event
     */
    public void onTickEvent(BiConsumer<BitfinexTickerSymbol, BitfinexTick> consumer) {
        this.tickConsumer = consumer;
    }
}
