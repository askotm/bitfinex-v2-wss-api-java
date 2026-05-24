package com.github.jnidzwetzki.bitfinex.v2.test.integration;

import org.junit.Assert;
import org.junit.Test;

import com.github.jnidzwetzki.bitfinex.v2.BitfinexApiCallbackRegistry;
import com.github.jnidzwetzki.bitfinex.v2.BitfinexWebsocketClient;
import com.github.jnidzwetzki.bitfinex.v2.BitfinexWebsocketConfiguration;
import com.github.jnidzwetzki.bitfinex.v2.SequenceNumberAuditor;
import com.github.jnidzwetzki.bitfinex.v2.SimpleBitfinexApiBroker;
import com.github.jnidzwetzki.bitfinex.v2.exception.BitfinexClientException;

public class AuthEndpointIntegrationTest extends AbstractPublicChannelIntegrationTest {

    @Override
    protected BitfinexWebsocketConfiguration createConfig() {
        final BitfinexWebsocketConfiguration config = new BitfinexWebsocketConfiguration();
        config.setWebsocketEndpointUrl(SimpleBitfinexApiBroker.BITFINEX_URI);
        return config;
    }

    @Test(expected = BitfinexClientException.class, timeout = 120_000)
    public void testAuthFailed() throws BitfinexClientException {
        final String KEY = "key";
        final String SECRET = "secret";

        final BitfinexWebsocketConfiguration config = new BitfinexWebsocketConfiguration();
        config.setApiCredentials(KEY, SECRET);
        final BitfinexWebsocketClient bitfinexClient = new SimpleBitfinexApiBroker(
                config, new BitfinexApiCallbackRegistry(), new SequenceNumberAuditor(), false);
        Assert.assertEquals(KEY, bitfinexClient.getConfiguration().getApiKey());
        Assert.assertEquals(SECRET, bitfinexClient.getConfiguration().getApiSecret());
        Assert.assertFalse(bitfinexClient.isAuthenticated());

        bitfinexClient.connect();

        Assert.fail();
        bitfinexClient.close();
    }
}
