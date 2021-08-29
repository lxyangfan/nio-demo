package com.frank.discard;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class DiscardClient {

    public static void main(String[] args) throws IOException{

        new DiscardClient().start();

    }

    public void start() throws IOException {
        SocketChannel clientChannel = SocketChannel.open();
        clientChannel.configureBlocking(false);

        InetSocketAddress remoteHostAndPort = new InetSocketAddress("127.0.0.1", 18888);
        clientChannel.connect(remoteHostAndPort);

        while(!clientChannel.finishConnect()) {
        }

        // send hello to channel
        ByteBuffer sendBuffer = ByteBuffer.allocate(2048);
        String content = "hello discard server";
        sendBuffer.put(content.getBytes("utf-8"));
        sendBuffer.flip();

        clientChannel.write(sendBuffer);
        clientChannel.shutdownInput();
        clientChannel.close();

    }

}
