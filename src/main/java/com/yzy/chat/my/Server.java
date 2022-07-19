package com.yzy.chat.my;
import com.yzy.util.ConcurrentHashSet;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;

public class Server {



    public static void main(String[] args) throws IOException {
        new Server(8080).start();
    }

    private final Selector selector=Selector.open();
    private final Set<SocketChannel> clients = new ConcurrentHashSet<>();


    public Server(Integer port) throws IOException {
        ServerSocketChannel server = ServerSocketChannel.open();
        server.configureBlocking(false);
        server.bind(new InetSocketAddress(port));
        server.register(selector, SelectionKey.OP_ACCEPT);
    }

    private void start() throws IOException {
        System.out.println("server start...");
        for (;;){
            int select = selector.select();
            if (select>0){
                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                while (iterator.hasNext()){
                    SelectionKey key = iterator.next();
                    iterator.remove();
                    if (key.isAcceptable()){
                        onAcceptEvent(key);
                    }else if (key.isReadable()){
                        onReadEvent(key);
                    }
                }
            }
        }

    }

    private void onAcceptEvent(SelectionKey key) throws IOException {
        ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
        SocketChannel sc = ssc.accept();
        sc.configureBlocking(false);
        sc.register(selector,SelectionKey.OP_READ);
        System.out.println("client connect："
                + sc.socket().getInetAddress().getHostName() + ":"
                + sc.socket().getPort());
    }


    private void onReadEvent(SelectionKey key) throws IOException {
        SocketChannel sc = (SocketChannel) key.channel();
        clients.add(sc);
        ByteBuffer bf = ByteBuffer.allocate(1024);
        try {
            int readLength = sc.read(bf);
            System.out.println("server listen："+new String(bf.array(), 0, readLength));
            bf.flip();
            //分发消息
            dispatcherMsg(sc, bf);
        }catch (IOException e){
            // 在读取客户端消息的时候，如果客户端关闭了就会抛出这个异常，不捕获程序就挂了
            System.out.println("client closed："+sc);
            // 这里不close的话会一直重复不能用的读事件，客户端close了，要把对应的chanel也close了
            sc.close();
            // 客户端表删除
            clients.remove(sc);
        }
    }

    private void dispatcherMsg(SocketChannel sc, ByteBuffer bf) throws IOException {
        for (SocketChannel client : clients) {
            if (client != sc) {
                client.write(bf);
            }
        }
    }

}
