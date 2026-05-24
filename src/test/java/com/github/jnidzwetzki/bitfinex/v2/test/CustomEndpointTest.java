package com.github.jnidzwetzki.bitfinex.v2.test;

import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.github.jnidzwetzki.bitfinex.v2.BitfinexClientFactory;
import com.github.jnidzwetzki.bitfinex.v2.BitfinexWebsocketClient;
import com.github.jnidzwetzki.bitfinex.v2.BitfinexWebsocketConfiguration;

public class CustomEndpointTest {

    private MockBitfinexWebsocketServer server;

    @Before
    public void startServer() throws Exception {
        server = new MockBitfinexWebsocketServer();
        server.startAndAwait();
    }

    @After
    public void stopServer() throws Exception {
        server.stop();
    }

    @Test
    public void testCustomWebsocketEndpointIsUsed() throws Exception {
        final BitfinexWebsocketConfiguration config = new BitfinexWebsocketConfiguration();
        config.setWebsocketEndpointUrl("ws://localhost:" + server.getBindPort() + "/ws/2");

        final BitfinexWebsocketClient client = BitfinexClientFactory.newSimpleClient(config);
        client.connect();

        Assert.assertTrue("Client did not connect", server.awaitConnection(5, TimeUnit.SECONDS));
        Assert.assertEquals("/ws/2", server.getLastHandshake().getResourceDescriptor());
        Assert.assertEquals(1, server.getConnectionCount());

        client.close();
    }
}
