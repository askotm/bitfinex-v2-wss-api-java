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

import com.github.jnidzwetzki.bitfinex.v2.BitfinexWebsocketConfiguration;
import com.github.jnidzwetzki.bitfinex.v2.SimpleBitfinexApiBroker;

public class BitfinexWebsocketConfigurationTest {

    private static final String CUSTOM_URL = "wss://custom.example.com/ws/2";
    private static final String DUMMY_KEY = "key";
    private static final String DUMMY_SECRET = "secret";

    @Test
    public void defaultEndpointIsPublic() {
        final BitfinexWebsocketConfiguration config = new BitfinexWebsocketConfiguration();
        Assert.assertEquals(SimpleBitfinexApiBroker.BITFINEX_URI_PUBLIC, config.getWebsocketEndpointUrl());
    }

    @Test
    public void setApiCredentialsSwitchesToAuthEndpoint() {
        final BitfinexWebsocketConfiguration config = new BitfinexWebsocketConfiguration();
        config.setApiCredentials(DUMMY_KEY, DUMMY_SECRET);
        Assert.assertEquals(SimpleBitfinexApiBroker.BITFINEX_URI, config.getWebsocketEndpointUrl());
    }

    @Test
    public void customEndpointPreservedAfterSetApiCredentials() {
        final BitfinexWebsocketConfiguration config = new BitfinexWebsocketConfiguration();
        config.setWebsocketEndpointUrl(CUSTOM_URL);
        config.setApiCredentials(DUMMY_KEY, DUMMY_SECRET);
        Assert.assertEquals(CUSTOM_URL, config.getWebsocketEndpointUrl());
    }

    @Test
    public void copyConstructorPreservesPublicEndpoint() {
        final BitfinexWebsocketConfiguration original = new BitfinexWebsocketConfiguration();
        final BitfinexWebsocketConfiguration copy = new BitfinexWebsocketConfiguration(original);
        Assert.assertEquals(SimpleBitfinexApiBroker.BITFINEX_URI_PUBLIC, copy.getWebsocketEndpointUrl());
    }

    @Test
    public void copyConstructorPreservesAuthEndpoint() {
        final BitfinexWebsocketConfiguration original = new BitfinexWebsocketConfiguration();
        original.setWebsocketEndpointUrl(SimpleBitfinexApiBroker.BITFINEX_URI);
        final BitfinexWebsocketConfiguration copy = new BitfinexWebsocketConfiguration(original);
        Assert.assertEquals(SimpleBitfinexApiBroker.BITFINEX_URI, copy.getWebsocketEndpointUrl());
    }
}
