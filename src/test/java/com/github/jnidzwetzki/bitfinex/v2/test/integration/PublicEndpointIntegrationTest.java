package com.github.jnidzwetzki.bitfinex.v2.test.integration;

import com.github.jnidzwetzki.bitfinex.v2.BitfinexWebsocketConfiguration;

public class PublicEndpointIntegrationTest extends AbstractPublicChannelIntegrationTest {

    @Override
    protected BitfinexWebsocketConfiguration createConfig() {
        return new BitfinexWebsocketConfiguration();
    }
}
