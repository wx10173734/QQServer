package com.lzc.qqserver.service;

import com.lzc.qqcommon.Message;
import com.lzc.qqcommon.MessageType;
import com.lzc.qqserver.utils.Utility;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Scanner;

/**
 * @title: SendNewToAllService
 * @Author luozouchen
 * @Date: 2022/10/13 1:32
 * @Version 1.0
 */
public class SendNewToAllService implements Runnable {


    @Override
    public void run() {
        //为了可以推送多次新闻，使用while
        while (true) {
            System.out.println("请输入服务器要推送的新闻[输入exit退出推送服务]");
            String news = Utility.readString(1000);
            if ("exit".equals(news)){
                break;
            }
            //构建一个消息，群发消息
            Message message = new Message();
            message.setSender("服务器");
            message.setContent(news);
            message.setMesType(MessageType.MESSAGE_TOALL_MES);
            message.setSendTime(new Date().toString());
            System.out.println("服务器推送消息给所有人 说: " + news);

            //遍历当前所有的通信线程，得到socket，并发送message对象
            HashMap<String, ServerConnectClientThread> hm = ManageClientThreads.getHm();
            Iterator<String> iterator = hm.keySet().iterator();
            while (iterator.hasNext()) {
                String onlineUserId = iterator.next().toString();
                ServerConnectClientThread serverConnectClientThread = hm.get(onlineUserId);
                try {
                    ObjectOutputStream oos = new ObjectOutputStream(serverConnectClientThread.getSocket().getOutputStream());
                    oos.writeObject(message);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }


            }
        }
    }
}
