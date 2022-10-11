package com.lzc.qqserver.service;

import com.lzc.qqcommon.Message;
import com.lzc.qqcommon.MessageType;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * @title: ServerConnectClientThread
 * @Author luozouchen
 * @Date: 2022/10/11 14:20
 * @Version 1.0
 * 该类对应的一个对象和某个客户端保持通信，
 */
public class ServerConnectClientThread extends Thread {
    private Socket socket;
    private String userId;//连接到服务端的用户id

    public ServerConnectClientThread(Socket socket, String userId) {
        this.socket = socket;
        this.userId = userId;
    }

    @Override
    public void run() {//这里线程处于run状态。可以发送/接收消息
        while (true) {
            try {
                System.out.println("服务端和客户端" + userId + "保持通信，读取数据...");
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                Message message = (Message) ois.readObject();
                //后面会使用message,根据message类型，做相应业务处理
                if (message.getMesType().equals(MessageType.MESSAGE_GET_ONLINE_FRIEND)) {
                    //客户端要在线用户列表
                    /*
                        在线用户列表形式:100 200 紫霞仙子
                     */
                    System.out.println(message.getSender() + "要在线用户列表");
                    String onlineUser = ManageClientThreads.getOnlineUser();
                    //返回message
                    //构建一个message对象，返回给客户端
                    Message message2 = new Message();
                    message2.setMesType(MessageType.MESSAGE_RET_ONLINE_FRIEND);
                    message2.setContent(onlineUser);
                    message2.setGetter(message.getSender());
                    //写入到数据通道socket,返回给客户端
                    ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                    oos.writeObject(message2);

                } else {
                    System.out.println("其他类型的message暂时不处理");
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        }
    }
}