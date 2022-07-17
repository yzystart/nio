package com.yzy;

import org.junit.Test;

import java.io.IOException;
import java.net.*;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class DataGramNioTest {




    @Test
    public void send() throws IOException, InterruptedException {

        DatagramChannel channel= DatagramChannel.open();
        InetSocketAddress inetSocketAddress = new InetSocketAddress("127.0.0.1", 9000);
        while (true){
            java.lang.String s="你好";
            channel.send(ByteBuffer.wrap(s.getBytes()),inetSocketAddress);
            System.out.println(new String(s.getBytes()));
            Thread.sleep(1000L);
        }


    }


    @Test
    public void receive() throws IOException {
        DatagramChannel channel= DatagramChannel.open();
        channel.bind(new InetSocketAddress(10086));
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        for (;;){
            channel.receive(buffer);
            byte[] array = buffer.array();
            StringBuilder builder=new StringBuilder();
            buffer.flip();
            System.out.println(new String(buffer.array()));
            buffer.clear();
        }


    }


}
