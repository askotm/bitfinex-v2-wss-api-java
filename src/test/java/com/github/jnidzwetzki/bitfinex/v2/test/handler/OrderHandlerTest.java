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

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.github.jnidzwetzki.bitfinex.v2.callback.channel.account.info.OrderHandler;
import com.github.jnidzwetzki.bitfinex.v2.entity.BitfinexApiKeyPermissions;
import com.github.jnidzwetzki.bitfinex.v2.entity.BitfinexOrderType;
import com.github.jnidzwetzki.bitfinex.v2.entity.BitfinexSubmittedOrder;
import com.github.jnidzwetzki.bitfinex.v2.entity.currency.BitfinexCurrencyPair;
import com.github.jnidzwetzki.bitfinex.v2.exception.BitfinexClientException;
import com.github.jnidzwetzki.bitfinex.v2.symbol.BitfinexSymbols;

public class OrderHandlerTest {

    @BeforeClass
    public static void registerDefaultCurrencyPairs() {
        if (BitfinexCurrencyPair.values().size() < 10) {
            BitfinexCurrencyPair.unregisterAll();
            BitfinexCurrencyPair.registerDefaults();
        }
    }

    @Test
    public void parsesIocOrderType() throws BitfinexClientException {
        assertOrderTypeParsed("IOC", BitfinexOrderType.IOC);
    }

    @Test
    public void parsesExchangeIocOrderType() throws BitfinexClientException {
        assertOrderTypeParsed("EXCHANGE IOC", BitfinexOrderType.EXCHANGE_IOC);
    }

    private void assertOrderTypeParsed(String protocolString, BitfinexOrderType expected) throws BitfinexClientException {
        final List<BitfinexSubmittedOrder> orders = parseOrder(protocolString);
        Assert.assertEquals(1, orders.size());
        Assert.assertEquals(expected, orders.get(0).getOrderType());
    }

    private List<BitfinexSubmittedOrder> parseOrder(String orderTypeString) throws BitfinexClientException {
        // Order array with the given type at index 8 (status ACTIVE, price 6800)
        final String json = "[123,null,1514956504945000,\"tBTCUSD\",1514956505134,"
                + "1514956505164,-1.0,-1.0,\"" + orderTypeString + "\",null,null,null,0,\"ACTIVE\","
                + "null,null,6800,0,null,null,null,null,null,0,0,0]";
        final JSONArray payload = new JSONArray(json);

        final List<BitfinexSubmittedOrder> received = new ArrayList<>();
        final OrderHandler handler = new OrderHandler(0,
                BitfinexSymbols.account(BitfinexApiKeyPermissions.ALL_PERMISSIONS, "key"));
        handler.onSubmittedOrderEvent((sym, orders) -> received.addAll(orders));
        handler.handleChannelData("on", payload);
        return received;
    }
}
