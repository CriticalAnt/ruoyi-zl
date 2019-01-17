package com.ruoyi.websocket.server;

import com.ruoyi.websocket.config.GetHttpSessionConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;
import javax.websocket.EndpointConfig;
import javax.websocket.OnClose;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
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
    private static final Logger log = LoggerFactory.getLogger(WebSocketServer.class);
    public static CopyOnWriteArraySet<WebSocketServer> webSocketSet = new CopyOnWriteArraySet<WebSocketServer>();
    private static ConcurrentHashMap<String, String> httpSessionId = new ConcurrentHashMap<>();
    private Session session;
    private HttpSession httpSession;

    @OnOpen
    public void onOpen(Session session, EndpointConfig config) {
        this.session = session;
        this.httpSession = (HttpSession) config.getUserProperties().get(HttpSession.class.getName());
        try {
            for (WebSocketServer server : webSocketSet) {
                if (server.httpSession.getId().equals(this.httpSession.getId())) {
                    server.session.close();
                    webSocketSet.remove(server);
                    break;
                }
            }
        } catch (Exception e) {
            log.error("WebSocketServer::onOpen:" + e.getMessage());
        }
        webSocketSet.add(this);     //加入set中
    }

    @OnClose
    public void onClose(Session session) {
        try {
            webSocketSet.remove(this);
        } catch (Exception e) {
            log.error("WebSocketServer::onClose:" + e.getMessage());
        }
    }

    public static void sendMessage() {
        try {
            for (WebSocketServer server : webSocketSet) {
                server.session.getBasicRemote().sendText("refresh");
            }
        } catch (Exception e) {
            log.error("WebSocketServer::sendMessage:" + e.getMessage());
        }
    }

    public Session getSession() {
        return session;
    }
}
