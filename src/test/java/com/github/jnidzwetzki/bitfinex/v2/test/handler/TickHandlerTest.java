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
package com.github.jnidzwetzki.bitfinex.v2.test.handler;

import java.util.concurrent.ExecutorService;

import org.json.JSONArray;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

import com.github.jnidzwetzki.bitfinex.v2.BitfinexApiCallbackRegistry;
import com.github.jnidzwetzki.bitfinex.v2.BitfinexWebsocketClient;
import com.github.jnidzwetzki.bitfinex.v2.SimpleBitfinexApiBroker;
import com.github.jnidzwetzki.bitfinex.v2.callback.channel.TickHandler;
import com.github.jnidzwetzki.bitfinex.v2.entity.BitfinexFundingTick;
import com.github.jnidzwetzki.bitfinex.v2.entity.currency.BitfinexCurrencyPair;
import com.github.jnidzwetzki.bitfinex.v2.entity.currency.BitfinexFundingCurrency;
import com.github.jnidzwetzki.bitfinex.v2.exception.BitfinexClientException;
import com.github.jnidzwetzki.bitfinex.v2.manager.QuoteManager;
import com.github.jnidzwetzki.bitfinex.v2.symbol.BitfinexSymbols;
import com.github.jnidzwetzki.bitfinex.v2.symbol.BitfinexTickerSymbol;
import com.google.common.util.concurrent.MoreExecutors;


public class TickHandlerTest {

	@BeforeClass
	public static void registerDefaultCurrencyPairs() {
		if(BitfinexCurrencyPair.values().size() < 10) {
			BitfinexCurrencyPair.unregisterAll();
			BitfinexCurrencyPair.registerDefaults();	
		}
	}

    /**
     * The delta for double compares
     */
    private static final double DELTA = 0.001;

    /**
     * Creates a QuoteManager wired to a mock broker, suitable for driving a TickHandler.
     */
    private static QuoteManager newQuoteManager() {
        final ExecutorService executor = MoreExecutors.newDirectExecutorService();
        final BitfinexWebsocketClient broker = Mockito.mock(SimpleBitfinexApiBroker.class);
        Mockito.when(broker.getCallbacks()).thenReturn(new BitfinexApiCallbackRegistry());
        final QuoteManager manager = new QuoteManager(broker, executor);
        Mockito.when(broker.getQuoteManager()).thenReturn(manager);
        return manager;
    }

    /**
     * Test the parsing of one tick
     *
     * @throws BitfinexClientException
     */
    @Test
    public void testTickUpdateAndNotify() throws BitfinexClientException {

        final JSONArray jsonArray = new JSONArray(
                "[26123,41.4645776,26129,33.68138507,2931,0.2231,26129,144327.10936387,26149,13139]");

        final BitfinexTickerSymbol symbol = BitfinexSymbols.ticker(BitfinexCurrencyPair.of("BTC", "USD"));
        final QuoteManager tickerManager = newQuoteManager();

        tickerManager.registerTickCallback(symbol, (s, c) -> {
            Assert.assertEquals(symbol, s);
            Assert.assertEquals(26123d, c.getBid().doubleValue(), DELTA);
            Assert.assertEquals(41.4645776, c.getBidSize().doubleValue(), DELTA);
            Assert.assertEquals(26129d, c.getAsk().doubleValue(), DELTA);
            Assert.assertEquals(33.68138507, c.getAskSize().doubleValue(), DELTA);
            Assert.assertEquals(2931d, c.getDailyChange().doubleValue(), DELTA);
            Assert.assertEquals(0.2231, c.getDailyChangePerc().doubleValue(), DELTA);
            Assert.assertEquals(26129d, c.getLastPrice().doubleValue(), DELTA);
            Assert.assertEquals(144327.10936387, c.getVolume().doubleValue(), DELTA);
            Assert.assertEquals(26149d, c.getHigh().doubleValue(), DELTA);
            Assert.assertEquals(13139d, c.getLow().doubleValue(), DELTA);
        });

        Assert.assertEquals(-1, tickerManager.getHeartbeatForSymbol(symbol));

        final TickHandler tickHandler = new TickHandler(0, symbol);
        tickHandler.onTickEvent(tickerManager::handleNewTick);

        tickHandler.handleChannelData(null, jsonArray);

        Assert.assertTrue(tickerManager.getHeartbeatForSymbol(symbol) != -1);
    }

    /**
     * Funding ticker (fXXX) uses a 13-field layout (FRR, BID, BID_PERIOD, BID_SIZE, ASK, ASK_PERIOD,
     * ASK_SIZE, DAILY_CHANGE, DAILY_CHANGE_REL, LAST_PRICE, VOLUME, HIGH, LOW) — different from the
     * 10-field trading layout. Verifies each field lands in the correct BitfinexFundingTick slot.
     */
    @Test
    public void testFundingTickUpdateAndNotify() throws BitfinexClientException {
        final JSONArray jsonArray = new JSONArray(
                "[0.00035,0.00033,2,120000.0,0.00036,30,85000.0,0.000005,0.0145,0.00034,9800000.0,0.00038,0.00031]");

        final BitfinexTickerSymbol symbol = BitfinexSymbols.ticker(new BitfinexFundingCurrency("USD"));
        final QuoteManager tickerManager = newQuoteManager();

        tickerManager.registerTickCallback(symbol, (s, c) -> {
            Assert.assertEquals(symbol, s);
            Assert.assertSame(BitfinexFundingTick.class, c.getClass());
            final BitfinexFundingTick ft = (BitfinexFundingTick) c;
            Assert.assertEquals(0.00035, ft.getFrr().doubleValue(), DELTA);
            Assert.assertEquals(0.00033, ft.getBid().doubleValue(), DELTA);
            Assert.assertEquals(2.0, ft.getBidPeriod().doubleValue(), DELTA);
            Assert.assertEquals(120000.0, ft.getBidSize().doubleValue(), DELTA);
            Assert.assertEquals(0.00036, ft.getAsk().doubleValue(), DELTA);
            Assert.assertEquals(30.0, ft.getAskPeriod().doubleValue(), DELTA);
            Assert.assertEquals(85000.0, ft.getAskSize().doubleValue(), DELTA);
            Assert.assertEquals(0.000005, ft.getDailyChange().doubleValue(), DELTA);
            Assert.assertEquals(0.0145, ft.getDailyChangePerc().doubleValue(), DELTA);
            Assert.assertEquals(0.00034, ft.getLastPrice().doubleValue(), DELTA);
            Assert.assertEquals(9800000.0, ft.getVolume().doubleValue(), DELTA);
            Assert.assertEquals(0.00038, ft.getHigh().doubleValue(), DELTA);
            Assert.assertEquals(0.00031, ft.getLow().doubleValue(), DELTA);
        });

        final TickHandler tickHandler = new TickHandler(0, symbol);
        tickHandler.onTickEvent(tickerManager::handleNewTick);

        tickHandler.handleChannelData(null, jsonArray);
    }

}
