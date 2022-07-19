package com.yzy.chat.template;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class NioServer {

        /**
         * 聊天室成员列表：
         */
        Map<String, SocketChannel> memberChannels;

        /**
         * 端口
         */
        private static final int PORT = 8000;

        /**
         * 选择器
         */
        private Selector selector;

        /**
         * 管道
         */
        private ServerSocketChannel server;

        /**
         * 缓冲
         */
        private ByteBuffer buffer;

        public NioServer() throws IOException {
            // 初始化 Selector 选择器
            this.selector = Selector.open();
            // 初始化 Channel 通道
            this.server = getServerChannel(selector);
            // 初始化 Buffer 缓冲：1k
            this.buffer = ByteBuffer.allocate(1024);
            // 初始化聊天室成员列表
            memberChannels = new ConcurrentHashMap<>();
        }

        /**
         * 初始化Channel通道
         *
         * @param selector 选择器
         * @return ServerSocketChannel
         * @throws IOException
         */
        private ServerSocketChannel getServerChannel(Selector selector) throws IOException {
            // 开辟一个 Channel 通道
            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();

            // 通道设置为非阻塞模式
            serverSocketChannel.configureBlocking(false);

            // 通道注册绑定 Selector 选择器，通道中数据的事件类型为OP_ACCEPT
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

            // 通道绑定端口
            serverSocketChannel.socket().bind(new InetSocketAddress(PORT));

            return serverSocketChannel;
        }

        /**
         * 事件监听
         */
        public void listen() throws IOException {
            System.out.println("服务端启动......");
            try {
                // 据说用 while(true) 会多一个判断，用这种方式更好哈哈哈
                for (;;){



                    // 作用：至少需要有一个事件发生，否则（如果count == 0）就继续阻塞循环
                    int count = selector.select();
                    if (count == 0) {
                        continue;
                    }
                    // 获取 SelectorKey 的集合
                    Set<SelectionKey> keySet = selector.selectedKeys();
                    Iterator<SelectionKey> iterator = keySet.iterator();

                    while (iterator.hasNext()) {
                        // 当前事件对应的 SelectorKey
                        SelectionKey key = iterator.next();

                        // 删除当前事件：表示当前事件已经被消费了
                        iterator.remove();

                        // 接收事件已就绪：
                        if (key.isAcceptable()) {

                            // 通过key获取ServerSocketChannel
                            ServerSocketChannel server = (ServerSocketChannel) key.channel();

                            // 通过 ServerSocketChannel 获取SocketChannel
                            SocketChannel channel = server.accept();

                            // channel 设置为非阻塞模式
                            channel.configureBlocking(false);
                            // channel 绑定选择器，当前事件切换为 读就绪
                            channel.register(selector, SelectionKey.OP_READ);

                            // 从channel中获取Host、端口等信息
                            System.out.println("客户端连接："
                                    + channel.socket().getInetAddress().getHostName() + ":"
                                    + channel.socket().getPort());

                            // Read就绪事件
                        } else if (key.isReadable()) {

                            SocketChannel channel = (SocketChannel) key.channel();
                            // 用于解密消息内容
                            CharsetDecoder decoder = StandardCharsets.UTF_8.newDecoder();

                            // 将消息数据从通道 channel 读取到缓冲buffer
                            buffer.clear();
                            channel.read(buffer);
                            buffer.flip();
                            // 获取解密后的消息内容：
                            String msg = decoder.decode(buffer).toString();
                            if (!"".equals(msg)) {
                                System.out.println("收到：" + msg);

                                for (SocketChannel value : memberChannels.values()) {
                                    value.write(ByteBuffer.wrap("server send test".getBytes()));
                                }

                                if (msg.startsWith("username:")) {
                                    String username = msg.replaceAll("username:", "");
                                    memberChannels.put(username, channel);
                                    System.out.println("用户总数：" + memberChannels.size());
                                } else {
                                    // 转发消息给客户端
                                    String[] arr = msg.split(":");
                                    if (arr.length == 3) {
                                        // 发送者
                                        String from = arr[0];
                                        // 接收者
                                        String to = arr[1];
                                        // 发送内容
                                        String content = arr[2];
                                        System.out.println(from + " 发送给 " + to + " 的消息：" + content);

                                        if (memberChannels.containsKey(to)) {
                                            // 解密
                                            CharsetEncoder encoder = StandardCharsets.UTF_8.newEncoder();
                                            // 给接收者发送消息
                                            memberChannels.get(to).write(encoder.encode(CharBuffer.wrap(from + ":" + content)));
                                        }
                                    }
                                }
                            }

                        }
                    }
                }
            }catch (Exception e){
                System.out.println("服务端启动失败......");
                e.printStackTrace();
            }finally {
                try {
                    // 先关闭选择器，在关闭通道
                    // 调用 close() 方法将会关闭Selector，同时也会将关联的SelectionKey失效，但不会关闭Channel。
                    selector.close();
                    server.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    public static void main(String[] args) throws IOException {
        // 服务端启动：
        new NioServer().listen();
    }
}
