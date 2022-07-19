package com.yzy.chat.my;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;

public class Client{

    private final Selector selector=Selector.open();
    private final SocketChannel server;

    public Client(String ip, Integer port) throws IOException {
        server = SocketChannel.open();
        server.configureBlocking(false);
        server.connect(new InetSocketAddress(ip,port));
        server.register(selector, SelectionKey.OP_CONNECT);
        System.out.println("server:"+server);
    }

    private void startWrite() throws IOException {
        System.out.println("请输入：");
        Scanner scanner = new Scanner(System.in);
        for (;;){
            while (scanner.hasNext()){
                String sendMsg = scanner.next();
                server.write(ByteBuffer.wrap(sendMsg.getBytes(StandardCharsets.UTF_8)));
            }
        }
    }

    private void startRead() {
        new Thread(()->{
            for (;;){
                try {
                    int select = selector.select();
                    if (select>0){
                        Set<SelectionKey> keys = selector.selectedKeys();
                        Iterator<SelectionKey> iterator = keys.iterator();
                        while (iterator.hasNext()){
                            SelectionKey key = iterator.next();
                            iterator.remove();
                            if (key.isConnectable()){
                                onConnectEvent(key);
                            }else if (key.isReadable()){
                                SocketChannel sc = (SocketChannel) key.channel();
                                try {
                                    onReadEvent(sc);
                                }catch (IOException e){
                                    System.out.println("服务器关闭了连接。");
                                    sc.close();
                                }
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }

    private void onReadEvent(SocketChannel channel) throws IOException {
        ByteBuffer bf = ByteBuffer.allocate(1024);
        int readLength = channel.read(bf);
        System.out.println("receive:"+ new String(bf.array(),0,readLength));
    }

    private void onConnectEvent(SelectionKey key) throws IOException {
        SocketChannel sc = (SocketChannel) key.channel();
        if (sc.isConnectionPending()){
            sc.finishConnect();
        }
        sc.configureBlocking(false);
        sc.register(selector,SelectionKey.OP_READ);
        System.out.println("server connect:"+sc);
    }

    public static void main(String[] args) throws IOException {
        new Client("127.0.0.1",8080).start();
    }

    private void start() throws IOException {
        startRead();
        startWrite();
    }


}
