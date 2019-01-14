package com.ruoyi.websocket.config;

import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpServletRequest;

/**
 * @Author: wtao
 * @Date: 2019-01-13 4:47
 * @Version 1.0
 */
@WebListener
public class RequestListener implements ServletRequestListener {

    public RequestListener() {

    }

    @Override
    public void requestDestroyed(ServletRequestEvent servletRequestEvent) {

    }

    @Override
    public void requestInitialized(ServletRequestEvent sre) {

        ((HttpServletRequest) sre.getServletRequest()).getSession();
    }
}
