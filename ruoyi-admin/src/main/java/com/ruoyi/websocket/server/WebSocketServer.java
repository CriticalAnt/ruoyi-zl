package com.ruoyi.websocket.server;

import com.ruoyi.websocket.config.GetHttpSessionConfigurator;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;
import javax.websocket.EndpointConfig;
import javax.websocket.OnClose;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @Author: wtao
 * @Date: 2019-01-09 22:12
 * @Version 1.0
 */

@Component
@ServerEndpoint(value = "/deviceInfo.do", configurator = GetHttpSessionConfigurator.class)
public class WebSocketServer {

    public static CopyOnWriteArraySet<WebSocketServer> webSocketSet = new CopyOnWriteArraySet<WebSocketServer>();
    private static ConcurrentHashMap<String, String> httpSessionId = new ConcurrentHashMap<>();
    private Session session;
    private HttpSession httpSession;

    @OnOpen
    public void onOpen(Session session, EndpointConfig config) throws IOException {
        this.session = session;
        this.httpSession = (HttpSession) config.getUserProperties().get(HttpSession.class.getName());
        for (WebSocketServer server : webSocketSet) {
            if (server.httpSession.getId().equals(this.httpSession.getId())) {
                session.close();
                return;
            }
        }
        webSocketSet.add(this);     //加入set中
//        try {
//            sendMessage("连接成功");
//        } catch (IOException e) {
//            System.out.println("websocket IO异常");
//            webSocketSet.remove(this);
//        }
    }

    @OnClose
    public void onClose(Session session) throws IOException {
        System.out.println("连接关闭");
        webSocketSet.remove(this);
    }

    public static void sendMessage() throws IOException {
//        List<Map<String, String>> mapList = new ArrayList<>();
//        List<SysDevice> devices = deviceService.findAll();
//        Map<String, String> map = new HashMap<>();
//        for (Map.Entry<String, String> entry : ConstantState.registeredCode.entrySet())
//            map.put(entry.getKey(), entry.getValue());
//        for (SysDevice device : devices) {
//            Map<String, String> res = new HashMap<>();
//            res.put("id", String.valueOf(device.getId()));
//            res.put("devNum", device.getDevNum());
//            res.put("devName", device.getDevName());
//            if ("1".equals(map.get(device.getCode()))) {
//                res.put("status", "1");
//            } else {
//                res.put("status", "0");
//            }
//            mapList.add(res);
//        }
        for (WebSocketServer server : webSocketSet) {
            server.session.getBasicRemote().sendText("123");
        }
//        this.session.getBasicRemote().sendText("");
    }

    public Session getSession() {
        return session;
    }
}
