package com.yzy;


import org.junit.Test;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;

public class FileNioTest {

    @Test
    public void fileWriteTest() throws IOException{
        RandomAccessFile file=new RandomAccessFile("D:\\study\\nio\\src\\main\\java\\com\\yzy\\test.txt","rw");
        FileChannel channel = file.getChannel();
        ByteBuffer buffer = ByteBuffer.allocate(1024);

        String s = "写入测试。";
        buffer.put(s.getBytes(StandardCharsets.UTF_8));
        buffer.flip(); // 将指针重置到头，来写入数据
        while (buffer.hasRemaining()){
            int write = channel.write(buffer);
            System.out.println("写入了"+write);
        }
    }


    @Test
    public void fileWriteCopyTest() throws IOException{


        RandomAccessFile file=new RandomAccessFile("D:\\study\\nio\\src\\main\\java\\com\\yzy\\test.jpg","r");

        RandomAccessFile file2=new RandomAccessFile("D:\\study\\nio\\src\\main\\java\\com\\yzy\\test2.jpg","rw");

        FileChannel channel = file.getChannel();

        FileChannel channel2 = file2.getChannel();

        ByteBuffer buffer = ByteBuffer.allocate(1024);


        int byteRead = channel.read(buffer);

        while (byteRead!=-1){
            System.out.println("读取了"+byteRead+"个字节");
            /**
             *         limit = position;
             *         position = 0;
             *         mark = -1;
             */
            buffer.flip(); // 切换读写模式
            while (buffer.hasRemaining()){ // position < limit; 表示还有数据可以读取。
                channel2.write(buffer);
            }
            //全部读取完后就会变成  position = limit
            /**
             * position = 0;
             * limit = capacity;
             * mark = -1;
             *
             */
            buffer.clear();// 回归到初始状态，才能再次读取 position=limit是不能再继续读的

            byteRead=channel.read(buffer);
        }

        file2.close();

    }


    @Test
    public void fileReadTest() throws IOException {

        RandomAccessFile file=new RandomAccessFile("D:\\study\\nio\\src\\main\\java\\com\\yzy\\test.txt","r");

        FileChannel channel = file.getChannel();

        ByteBuffer buffer = ByteBuffer.allocate(1024);


        int byteRead = channel.read(buffer);

        while (byteRead!=-1){
            System.out.println("读取了"+byteRead+"个字节");
            /**
             *         limit = position;
             *         position = 0;
             *         mark = -1;
             */
            buffer.flip(); // 切换读写模式
            while (buffer.hasRemaining()){ // position < limit; 表示还有数据可以读取。
                System.out.println((char)buffer.get()); // get position + 1
            }
            //全部读取完后就会变成  position = limit
            /**
             * position = 0;
             * limit = capacity;
             * mark = -1;
             *
             */
            buffer.clear();// 回归到初始状态，才能再次读取 position=limit是不能再继续读的

            byteRead=channel.read(buffer);
        }

    }
}
