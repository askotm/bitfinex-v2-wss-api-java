package com.github.jnidzwetzki.bitfinex.v2.test;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

public class MockBitfinexWebsocketServer extends WebSocketServer {

    private final CountDownLatch startedLatch = new CountDownLatch(1);
    private final CountDownLatch connectedLatch = new CountDownLatch(1);

    private volatile ClientHandshake lastHandshake;
    private final List<String> receivedMessages = Collections.synchronizedList(new ArrayList<>());

    public MockBitfinexWebsocketServer() {
        super(new InetSocketAddress(0));
    }

    @Override
    public void onStart() {
        startedLatch.countDown();
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        lastHandshake = handshake;
        conn.send("{\"event\":\"info\",\"version\":2,\"platform\":{\"status\":1}}");
        connectedLatch.countDown();
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {}

    @Override
    public void onMessage(WebSocket conn, String message) {
        receivedMessages.add(message);
    }

    @Override
    public void onError(WebSocket conn, Exception e) {}

    public void startAndAwait() throws InterruptedException {
        start();
        startedLatch.await(5, TimeUnit.SECONDS);
    }

    public int getBindPort() {
        return getPort();
    }

    public boolean awaitConnection(long timeout, TimeUnit unit) throws InterruptedException {
        return connectedLatch.await(timeout, unit);
    }

    public ClientHandshake getLastHandshake() {
        return lastHandshake;
    }

    public List<String> getReceivedMessages() {
        return Collections.unmodifiableList(receivedMessages);
    }

    public int getConnectionCount() {
        return getConnections().size();
    }
}
