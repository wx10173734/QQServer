package com.lzc.qqserver.service;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 * @title: ManageClientThreads
 * @Author luozouchen
 * @Date: 2022/10/11 19:32
 * @Version 1.0
 * 该类用于管理和客户端通讯的线程
 */
public class ManageClientThreads {
    private static HashMap<String, ServerConnectClientThread> hm = new HashMap<>();

    //添加线程对象到hm集合
    public static void addClientThread(String userId, ServerConnectClientThread serverConnectClientThread) {
        hm.put(userId, serverConnectClientThread);
    }

    //根据userId返回一个 serverConnectClientThread 线程
    public static ServerConnectClientThread getServerConnectClientThread(String userId) {
        return hm.get(userId);
    }

    //这里编写方法，可以返回在线用户列表
    public static String getOnlineUser() {
        //集合遍历，遍历hashMap的key
        Iterator<String> iterator = hm.keySet().iterator();
        String onlineUserList = "";
        while (iterator.hasNext()) {
            onlineUserList += iterator.next().toString() + " ";

        }
        return onlineUserList;
    }
}
