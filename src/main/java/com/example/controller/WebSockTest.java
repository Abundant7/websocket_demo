package com.example.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @ServerEndPoint 注解是一个类层次的注解，它的功能主要是将目前的类定义成一个websocket服务器端，
 * 注解的值将被用于监听用户连接的终端访问URL地址，客户端可以通过这个URL连接到websocket服务器端
 */
@ServerEndpoint("/websocket")
@Component
public class WebSockTest {
    private static int onlineCount=0;
    private static CopyOnWriteArrayList<WebSockTest> webSocketSet=new CopyOnWriteArrayList<WebSockTest>();
    private Session session;
    public void sendMessage(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
    }
    @OnOpen
    public void onOpen(Session session){

        this.session=session;
        webSocketSet.add(this);//加入set中
        addOnlineCount();

        for (WebSockTest item:webSocketSet){
            try {
                if(item.session != session)
                item.sendMessage("客户端"+session.getId()+"加入连接！当前在线人数为"+getOnlineCount());
                else
                item.sendMessage("当前在线人数为"+getOnlineCount()+"  您的客户端ID为："+session.getId());

            } catch (IOException e) {
                e.printStackTrace();
                continue;
            }
        }


        System.out.println("有新连接加入！当前在线人数为"+getOnlineCount());
    }

    @OnClose
    public void onClose(){
        webSocketSet.remove(this);
        subOnlineCount();
        System.out.println("有一连接关闭！当前在线人数为" + getOnlineCount());
    }

    @OnMessage
    public void onMessage(String message,Session session) throws JsonProcessingException {
        System.out.println("来自客户端"+session.getId()+"的消息："+message);
        //        群发消息
        for (WebSockTest item:webSocketSet){
            try {
                if(item.session != session)
                item.sendMessage("客户端"+session.getId()+"："+message);
                else
                item.sendMessage("我："+message);

            } catch (IOException e) {
                e.printStackTrace();
                continue;
            }
        }
    }

    @OnError
    public void onError(Session session, Throwable throwable){
        System.out.println("发生错误！");
        throwable.printStackTrace();
    }
    //   下面是自定义的一些方法
    /*public void sendMessage(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
    }
*/
    public static synchronized int getOnlineCount(){
        return onlineCount;
    }
    public static synchronized void addOnlineCount(){
        WebSockTest.onlineCount++;
    }
    public static synchronized void subOnlineCount(){
        WebSockTest.onlineCount--;
    }
}