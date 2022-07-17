package com.yzy;

import org.junit.Test;
import sun.nio.ch.DirectBuffer;

import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class BufferTest {



    @Test
    public void db() throws FileNotFoundException {
        RandomAccessFile file=new RandomAccessFile("D:\\study\\nio\\src\\main\\java\\com\\yzy\\test.txt","rw");

        ByteBuffer allocate = MappedByteBuffer.allocate(1024);
        FileChannel channel = file.getChannel();
    }
}
