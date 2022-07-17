package com.yzy;

import com.sun.org.apache.xpath.internal.operations.String;
import org.junit.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class SocketNioTest {



    @Test
    public void test1() throws IOException {

        ServerSocketChannel channel = ServerSocketChannel.open();
        channel.bind(new InetSocketAddress(8088));
        channel.configureBlocking(false); //设置为非阻塞->channel.accept() 将不会阻塞
        while (true){
            SocketChannel socketChannel = channel.accept();
            if (socketChannel!=null){
                ByteBuffer buffer = ByteBuffer.allocate(1024);
                socketChannel.read(buffer);
                buffer.flip();
//                while (buffer.hasRemaining()){
//                    s.append(buffer.getChar());
//                }
                if (buffer.hasRemaining()){
                    byte[] array = buffer.array();
                    StringBuilder s = new StringBuilder();
                    for (byte b : array) {
                        s.append((char)b);
                    }
                    System.out.println(s.toString());
                    buffer.clear();

                }
            }
        }


    }
}
