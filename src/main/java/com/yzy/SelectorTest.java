package com.yzy;

import org.junit.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Set;

public class SelectorTest {

    @Test
    public void client() throws IOException {
        SocketChannel sc = SocketChannel.open();
//        sc.bind(new InetSocketAddress("127.0.0.1",8000));
//        sc.send(ByteBuffer.wrap("测试".getBytes(StandardCharsets.UTF_8)),new InetSocketAddress("127.0.0.1",8000));

        sc.write(ByteBuffer.wrap("测试".getBytes(StandardCharsets.UTF_8)));
    }


    @Test
    public void server2() throws IOException {
        // 创建NIO ServerSocketChannel
        ServerSocketChannel serverSocket = ServerSocketChannel.open();
        serverSocket.socket().bind(new InetSocketAddress(9000));
        // 设置ServerSocketChannel为非阻塞
        serverSocket.configureBlocking(false);
        //打开Selector处理Channel，即创建epoll，多路复用器
        Selector selector = Selector.open(); // selector其实是c语言实现的底层epoll对象的封装
        // 把ServerSocketChannel注册到selector中，并且selector对客户端的连接操作感兴趣
        SelectionKey selectionKey = serverSocket.register(selector, SelectionKey.OP_ACCEPT);
        System.out.println("服务启动成功！");

        while(true)
        {
            //阻塞等待需要处理的事件发生
            selector.select();
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectionKeys.iterator();
            while (iterator.hasNext())
            {
                SelectionKey key = iterator.next();
                if (key.isAcceptable()) // 如果是OP_ACCEPT事件，则进行连接获取和事件注册
                {
                    ServerSocketChannel server = (ServerSocketChannel) key.channel(); //连接获取
                    SocketChannel socketChannel = server.accept(); // 连接获取
                    socketChannel.configureBlocking(false); // 设置为非阻塞
                    SelectionKey selKey = socketChannel.register(selector, SelectionKey.OP_READ); //这里只注册了读事件，如果需要给客户端写数据，则需要注册写事件
                    System.out.println("客户端连接成功！");
                }else if(key.isReadable()) //如果是OP_READ事件，则进行读取和打印
                {
                    SocketChannel socketChannel = (SocketChannel) key.channel();
                    ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                    int len = socketChannel.read(byteBuffer);
                    if (len > 0) //如果有数据，则打印数据
                    {
                        System.out.println("接受到客户端数据"+new String(byteBuffer.array()));
                    }else if(len==-1) //如果客户端断开连接，关闭socket
                    {
                        System.out.println("客户端断开连接！");
                        socketChannel.close();
                    }
                }
                // 从事件集合中删除本次处理的key，防止下次select重复处理
                iterator.remove();



            }
        }


    }

    @Test
    public void server() throws IOException {

        Selector selector = Selector.open();

        ServerSocketChannel ssc = ServerSocketChannel.open();

        ssc.configureBlocking(false);

        ssc.bind(new InetSocketAddress(8080));

        ssc.register(selector, SelectionKey.OP_ACCEPT);

        while (selector.select()>0){//The number of keys, possibly zero, whose ready-operation sets were updated

                Set<SelectionKey> keys = selector.selectedKeys();

                Iterator<SelectionKey> iterator = keys.iterator();

                while (iterator.hasNext()){
                    SelectionKey key = iterator.next();
                    iterator.remove();
                    int x=0;
                    if (key.isAcceptable()){
                        ServerSocketChannel channel = (ServerSocketChannel)key.channel();
                        System.out.println("对象"+channel.toString());
                        SocketChannel accept = channel.accept();
                        accept.configureBlocking(false);

                        accept.register(selector,SelectionKey.OP_READ);


                    }else if (key.isReadable()){
                        SocketChannel channel = (SocketChannel)key.channel();
                        ByteBuffer buffer = ByteBuffer.allocate(1024);
                        System.out.println("触发了读事件");
                        channel.configureBlocking(false);
                        int l;
                        if ((l=channel.read(buffer))>0){
                            System.out.println(new String(buffer.array(),0,l));
                            buffer.clear();
                        }else if (l==-1){
                            channel.close();
                        }
//                        channel.register(selector,SelectionKey.OP_WRITE);
                    }else if (key.isWritable()){
                        SocketChannel channel = (SocketChannel)key.channel();
                        ByteBuffer buffer = ByteBuffer.allocate(1024);
                        channel.configureBlocking(false);
                        System.out.println("出发了写事件");
                        channel.write(ByteBuffer.wrap("写事件".getBytes(StandardCharsets.UTF_8)));

                    }else if (key.isConnectable()){
                        System.out.println("end");
                    }

                }

        }


    }
}
