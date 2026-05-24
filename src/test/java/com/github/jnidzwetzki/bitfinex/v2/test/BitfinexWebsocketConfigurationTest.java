package com.github.jnidzwetzki.bitfinex.v2.test;

import org.junit.Assert;
import org.junit.Test;

import com.github.jnidzwetzki.bitfinex.v2.BitfinexWebsocketConfiguration;
import com.github.jnidzwetzki.bitfinex.v2.SimpleBitfinexApiBroker;

public class BitfinexWebsocketConfigurationTest {

    @Test
    public void defaultEndpointIsPublic() {
        final BitfinexWebsocketConfiguration config = new BitfinexWebsocketConfiguration();
        Assert.assertEquals(SimpleBitfinexApiBroker.BITFINEX_URI_PUBLIC, config.getWebsocketEndpointUrl());
    }

    @Test
    public void setApiCredentialsSwitchesToAuthEndpoint() {
        final BitfinexWebsocketConfiguration config = new BitfinexWebsocketConfiguration();
        config.setApiCredentials("key", "secret");
        Assert.assertEquals(SimpleBitfinexApiBroker.BITFINEX_URI, config.getWebsocketEndpointUrl());
    }

    @Test
    public void customEndpointPreservedAfterSetApiCredentials() {
        final BitfinexWebsocketConfiguration config = new BitfinexWebsocketConfiguration();
        config.setWebsocketEndpointUrl("wss://custom.example.com/ws/2");
        config.setApiCredentials("key", "secret");
        Assert.assertEquals("wss://custom.example.com/ws/2", config.getWebsocketEndpointUrl());
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
