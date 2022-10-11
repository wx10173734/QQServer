package com.lzc.qqframe;

import com.lzc.qqserver.service.QQServer;

/**
 * @title: QQFrame
 * @Author luozouchen
 * @Date: 2022/10/11 19:45
 * @Version 1.0
 * 该类创建一个QQServer对象，启动后台的服务
 */
public class QQFrame {
    public static void main(String[] args) {
        new QQServer();
    }
}
