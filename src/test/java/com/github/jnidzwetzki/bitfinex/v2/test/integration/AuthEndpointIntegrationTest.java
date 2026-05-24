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
package com.github.jnidzwetzki.bitfinex.v2.test.integration;

import org.junit.Assert;
import org.junit.Test;

import com.github.jnidzwetzki.bitfinex.v2.BitfinexApiCallbackRegistry;
import com.github.jnidzwetzki.bitfinex.v2.BitfinexWebsocketClient;
import com.github.jnidzwetzki.bitfinex.v2.BitfinexWebsocketConfiguration;
import com.github.jnidzwetzki.bitfinex.v2.SequenceNumberAuditor;
import com.github.jnidzwetzki.bitfinex.v2.SimpleBitfinexApiBroker;
import com.github.jnidzwetzki.bitfinex.v2.exception.BitfinexClientException;

/**
 * Runs the public-channel integration suite against the authenticated WSS endpoint,
 * plus an auth-failure test using bogus credentials.
 */
public class AuthEndpointIntegrationTest extends AbstractPublicChannelIntegrationTest {

    private static final String DUMMY_KEY = "key";
    private static final String DUMMY_SECRET = "secret";

    @Override
    protected BitfinexWebsocketConfiguration createConfig() {
        final BitfinexWebsocketConfiguration config = new BitfinexWebsocketConfiguration();
        config.setWebsocketEndpointUrl(SimpleBitfinexApiBroker.BITFINEX_URI);
        return config;
    }

    /**
     * Connecting with bogus credentials must surface as a {@link BitfinexClientException}.
     */
    @Test(expected = BitfinexClientException.class, timeout = 120_000)
    public void testAuthFailed() throws BitfinexClientException {
        final BitfinexWebsocketConfiguration config = new BitfinexWebsocketConfiguration();
        config.setApiCredentials(DUMMY_KEY, DUMMY_SECRET);

        final BitfinexWebsocketClient bitfinexClient = new SimpleBitfinexApiBroker(
                config, new BitfinexApiCallbackRegistry(), new SequenceNumberAuditor(), false);

        Assert.assertEquals(DUMMY_KEY, bitfinexClient.getConfiguration().getApiKey());
        Assert.assertEquals(DUMMY_SECRET, bitfinexClient.getConfiguration().getApiSecret());
        Assert.assertFalse(bitfinexClient.isAuthenticated());

        try {
            bitfinexClient.connect();
            Assert.fail("connect() must throw BitfinexClientException for bogus credentials");
        } finally {
            bitfinexClient.close();
        }
    }
}
