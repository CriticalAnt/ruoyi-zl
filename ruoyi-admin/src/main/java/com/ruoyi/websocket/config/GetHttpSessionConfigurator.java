package com.ruoyi.websocket.config;


import javax.servlet.http.HttpSession;
import javax.websocket.HandshakeResponse;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.ServerEndpointConfig;

/**
 * @Author: wtao
 * @Date: 2019-01-13 4:42
 * @Version 1.0
 */
public class GetHttpSessionConfigurator extends ServerEndpointConfig.Configurator {
    @Override
    public void modifyHandshake(ServerEndpointConfig sec, HandshakeRequest request, HandshakeResponse response) {

        HttpSession httpSession = (HttpSession) request.getHttpSession();
        sec.getUserProperties().put(HttpSession.class.getName(), httpSession);
    }
}
