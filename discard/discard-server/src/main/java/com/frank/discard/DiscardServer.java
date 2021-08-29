package com.frank.discard;

import static java.nio.channels.SelectionKey.OP_ACCEPT;
import static java.nio.channels.SelectionKey.OP_READ;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Set;

public class DiscardServer {

    public static void main(String[] args) throws IOException {
        new DiscardServer().start();
    }


    public void start() throws IOException {
        // server socket channel to listen

        ServerSocketChannel server = ServerSocketChannel.open();
        server.configureBlocking(false);

        InetSocketAddress hostAndPort = new InetSocketAddress("127.0.0.1", 18888);
        server.bind(hostAndPort);

        Selector selector = Selector.open();
        server.register(selector, OP_ACCEPT);

        System.out.println("start to select IO events");

        while (selector.select() > 0) {
            System.out.println("Get IO Events...");
            Set<SelectionKey> interestedIOKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = interestedIOKeys.iterator();
            while (iterator.hasNext()) {
                SelectionKey ioKey = iterator.next();
                if (ioKey.isAcceptable()) {
                    // 来了新连接，将它的 socket channel READ 事件注册到 selector
                    System.out.println("[OP_ACCEPT] new client come in");
                    SocketChannel clientChannel = server.accept();
                    clientChannel.configureBlocking(false);
                    clientChannel.register(selector, OP_READ);
                } else if (ioKey.isReadable()) {
                    // read buffer from channel
                    System.out.println("[OP_READ] ready to read from buffer");
                    SocketChannel clientChannel = (SocketChannel)ioKey.channel();
                    clientChannel.configureBlocking(false);
                    ByteBuffer readBuffer = ByteBuffer.allocate(2048);
                    int len = 0;
                    while((len = clientChannel.read(readBuffer)) != -1) {
                        readBuffer.flip();
                        byte[] content = new byte[len];
                        readBuffer.get(content);
                        System.out.println(new String(content, Charset.forName("utf-8")));
                        readBuffer.clear();
                    }
                    clientChannel.close();
                }
                iterator.remove();
            }
        }
        System.out.println("server shutdown");
        server.close();
    }


}
