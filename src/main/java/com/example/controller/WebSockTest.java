package com.example.controller;

import com.example.pojo.Message;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import com.alibaba.fastjson.JSONObject;
/**
 * @ServerEndPoint 注解是一个类层次的注解，它的功能主要是将目前的类定义成一个websocket服务器端，
 * 注解的值将被用于监听用户连接的终端访问URL地址，客户端可以通过这个URL连接到websocket服务器端
 */
@Log4j2
@ServerEndpoint("/websocket")
@Component
public class WebSockTest {
    private static int onlineCount=0;
    private static Map<String, Session> webSocketSet=new ConcurrentHashMap<>();
    private Session session;

    @OnOpen
    public void onOpen(Session session){

    }

    @OnClose
    public void closeSession(Session session, CloseReason closeReason)
    {
        System.out.println(closeReason.toString());
        //记得移除相对应的session
        webSocketSet.remove(session.getId());

        sendAll("[" + session.getId() + "]离开了房间");
    }

    @OnMessage
    public void onMessage(String message,Session session) throws IOException {

        //当前session对应用户用户名
        String username = (String) session.getUserProperties().get("username");
        //创建base64编解码对象
        Base64.Decoder decoder = Base64.getDecoder();
        Base64.Encoder encoder = Base64.getEncoder();
        //密钥
        String desKey = "D3eU9n7t";
        //临时消息
        String tmpMsg;
        //
        String allDecodeMsg = null;
        //时间处理
        LocalTime time = LocalTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        //使用 fastjson 解析 json 字符串
        final Message data = JSONObject.parseObject(message, Message.class);
        //响应的信息
        final Message response = Message.builder()
                .operation(data.getOperation())         //将请求的 operation 放入
                .build();

        //获取原始消息
        String base64encodedString = data.getMsg();
        byte[] base64decodedBytes = null;

        if(!Objects.equals(data.getOperation(), "base64")){
            base64decodedBytes = Base64.getDecoder().decode(base64encodedString);
            allDecodeMsg = mydes.decrypt(new String(base64decodedBytes, "utf-8"),desKey);
        }



        //根据不同的 operation 执行不同的操作
        switch (data.getOperation()) {

            //进入聊天室后保存用户名
            case "heart":

                response.setMsg(time.format(formatter)+"[" + username + "]10001\n<br/>[" + username + "]心跳回应：10008");
                sendTo(session,JSONObject.toJSONString(response));
                break;
            case "tip":

                webSocketSet.put(session.getId(), session);
                //allDecodeMsg = mydes.decrypt(new String(base64decodedBytes, "utf-8"),desKey);
                //session.getUserProperties().put("username", new String(base64decodedBytes, "utf-8"));
                session.getUserProperties().put("username", allDecodeMsg);
                tmpMsg =time.format(formatter)+"[" + allDecodeMsg + "]进入房间";
                tmpMsg = mydes.encrypt(tmpMsg,desKey);
                tmpMsg = encoder.encodeToString(tmpMsg.getBytes(StandardCharsets.UTF_8));
                response.setMsg(tmpMsg);
                sendAll(JSONObject.toJSONString(response));
                break;
            //发送消息
            case "msg":
                //allDecodeMsg = mydes.decrypt(new String(base64decodedBytes, "utf-8"),desKey);

                //response.setMsg(time.format(formatter)+"[" + username + "]: " + data.getMsg());
                //tmpMsg = time.format(formatter)+"[" + username + "]: " + new String(base64decodedBytes, "utf-8");
                tmpMsg = time.format(formatter)+"[" + username + "]: " + allDecodeMsg;
                tmpMsg = mydes.encrypt(tmpMsg,desKey);
                tmpMsg = encoder.encodeToString(tmpMsg.getBytes(StandardCharsets.UTF_8));
                response.setMsg(tmpMsg);
                sendAll(JSONObject.toJSONString(response));
                break;
            case "filename":
                //allDecodeMsg = mydes.decrypt(new String(base64decodedBytes, "utf-8"),desKey);
                //删除原有文件
                File file = new File("F:\\BaiduNetdiskDownload\\2023新版JavaWeb开发教程\\笔记\\" + allDecodeMsg);
                file.delete();
                log.info(file.getCanonicalPath());

                //保存文件信息
                session.getUserProperties().put("file", file);
                tmpMsg = time.format(formatter)+"文件【" + allDecodeMsg + "】开始上传";
                tmpMsg = mydes.encrypt(tmpMsg,desKey);
                tmpMsg = encoder.encodeToString(tmpMsg.getBytes(StandardCharsets.UTF_8));
                response.setMsg(tmpMsg);
                sendTo(session, JSONObject.toJSONString(response));
                break;

            case "base64":


                String messageData = data.getMsg();
                String deDesMessage = mydes.decrypt(messageData,desKey);
                String base64Data =  deDesMessage.split(",")[1];
                //String base64Data =  messageData.split(",")[1];
                byte[] bytes = decoder.decode(base64Data);
                final File lastFile = (File) session.getUserProperties().get("file");
                if (saveFile(lastFile, bytes)) {
                    response.setOperation("file-upload-success");
                    response.setMsg(bytes.length + "");
                    sendTo(session, JSONObject.toJSONString(response));
                }


        }

    }

    //响应字节流
   /* @OnMessage
    public void onMessage(Session session, byte[] message)
    {
        final Message response = new Message();
        LocalTime time = LocalTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");

        final File file = (File) session.getUserProperties().get("file");

        if (saveFile(file, message)) {
            response.setOperation("file-upload-success");
            response.setMsg(message.length + "");
            sendTo(session, JSONObject.toJSONString(response));
        }
        else {
            response.setOperation("file-upload-fail");
            response.setMsg(time.format(formatter)+"文件【" + file.getName() + "】上传失败");
            file.delete();
            sendTo(session, JSONObject.toJSONString(response));
        }
    }*/


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

    private void sendAll(String message)
    {
        for (Session s : webSocketSet.values()) {
            sendTo(s, message);
        }
    }




    private void sendTo(Session session, String message)
    {
        final RemoteEndpoint.Basic remote = session.getBasicRemote();
        try {
            //发送消息
            remote.sendText(message);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean saveFile(File file, byte[] message)
    {
        try (OutputStream os = new FileOutputStream(file, true)) {
            os.write(message, 0, message.length);
            return true;
        }
        catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

}