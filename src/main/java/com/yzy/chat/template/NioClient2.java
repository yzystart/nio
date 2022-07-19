package com.yzy.chat.template;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class NioClient2 {
    public static void main(String[] args) {
        // 当前客户端的用户名
        String username = "fengzeng";
        // 为当前客户端开辟一个线程
        ClientThread client = new ClientThread(username);
        client.start();

        // 输入输出流
        BufferedReader bfReader = new BufferedReader(new InputStreamReader(System.in));

        try {
            // 循环读取键盘输入
            String readline;
            while ((readline = bfReader.readLine()) != null) {
                if ("bye".equals(readline)) {
                    client.close();
                    System.exit(0);
                }
                // 发送消息
                client.send(username + ":" + readline);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
