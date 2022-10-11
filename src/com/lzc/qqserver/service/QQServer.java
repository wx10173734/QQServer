package com.lzc.qqserver.service;

import com.lzc.qqcommon.Message;
import com.lzc.qqcommon.MessageType;
import com.lzc.qqcommon.User;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @title: QQServer
 * @Author luozouchen
 * @Date: 2022/10/11 14:05
 * @Version 1.0
 * 这时服务器，在监听9999，等待客户端的链接，并保持通信
 */
public class QQServer {
    private ServerSocket serverSocket = null;
    //创建一个集合，存放多个用户，如果是这些用户登陆就认为是合法的
    //这里也可以使用ConcurrentHashMap ，可以处理并发的集合
    //hashmap 没有处理线程安全，因此在多线程情况下不安全
    //ConcurrentHashMap 处理的线程安全，即线程同步处理，在多线程处理下是安全的
    private static ConcurrentHashMap<String, User> validUsers = new ConcurrentHashMap<>();

    static {//在静态代码块，初始化validUsers
        validUsers.put("100", new User("100", "123456"));
        validUsers.put("200", new User("200", "123456"));
        validUsers.put("300", new User("300", "123456"));
        validUsers.put("至尊宝", new User("至尊宝", "123456"));
        validUsers.put("紫霞仙子", new User("紫霞仙子", "123456"));
        validUsers.put("罗邹晨", new User("罗邹晨", "123456"));
    }

    //验证用户是否有效的方法
    private boolean checkUser(String userId, String passwd) {
        User user = validUsers.get(userId);
        //过关斩将验证码
        if (user == null) {//说明userId没有存在 validUsers 的Key中
            return false;
        }
        if (!user.getPasswd().equals(passwd)) {//userId正确，但是密码错误
            return false;
        }
        return true;
    }


    public QQServer() {
        //注意端口可以写在配置文件中
        try {
            System.out.println("服务器端在9999端口监听");
            serverSocket = new ServerSocket(9999);

            while (true) {//当和某个客户端连接后，会继续监听，因此while
                Socket socket = serverSocket.accept();//如果没有客户端链接，就会阻塞在这里
                //得到socket关联的对象输入流
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                //得到socket关联的对象输出流
                ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                User u = (User) ois.readObject();//读取客户端发送的user对象
                //创建一个message,准备回复客户端
                Message message = new Message();
                //验证用户
                if (checkUser(u.getUserId(), u.getPasswd())) {//登陆成功
                    message.setMesType(MessageType.MESSAGE_LOGIN_SUCCEED);
                    //将message对象回复客户端
                    oos.writeObject(message);
                    //创建一个线程，和客户端保持通信,该线程需要持有socket对象
                    ServerConnectClientThread serverConnectClientThread = new ServerConnectClientThread(socket, u.getUserId());
                    //启动该线程，放入到一个集合中，进行管理。
                    serverConnectClientThread.start();
                    //把该线程对象，放入到一个集中中，进行管理
                    ManageClientThreads.addClientThread(u.getUserId(), serverConnectClientThread);
                } else {//登陆失败
                    System.out.println("用户 id=" + u.getUserId() + "密码 = " + u.getPasswd() + "验证失败");
                    message.setMesType(MessageType.MESSAGE_LOGIN_FAIL);
                    oos.writeObject(message);
                    //登陆失败，关闭socket
                    socket.close();

                }

            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            //如果服务端退出了while,说明服务器端不在监听，因此关闭ServerSocket
            try {
                serverSocket.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
