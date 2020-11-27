package com.ppdai.infrastructure.mq.client.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ppdai.infrastructure.mq.biz.common.util.JsonUtil;
import com.ppdai.infrastructure.mq.biz.dto.client.MsgNotifyRequest;
import com.ppdai.infrastructure.mq.biz.dto.proxy.ProxyRequest;
import com.ppdai.infrastructure.mq.client.MqClientTool;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;

public class MqHandler implements Handler {

    private Map<String, Method> maps = new HashMap<>();

    @Override
    public void addLifeCycleListener(Listener arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean isFailed() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isRunning() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isStarted() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isStarting() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isStopped() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isStopping() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void removeLifeCycleListener(Listener arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void start() throws Exception {
        // TODO Auto-generated method stub

    }

    @Override
    public void stop() throws Exception {
        // TODO Auto-generated method stub

    }

    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        StringBuilder sbHtml = new StringBuilder();
        try {
            if ("/mq/client/consume".equalsIgnoreCase(request.getRequestURI())) {
                String body = getBody(request);
                ProxyRequest request1 = JsonUtil.parseJson(body, ProxyRequest.class);
                sbHtml.append(JsonUtil.toJsonNull(MqClientTool.proxy(request1)));
            }
            if ("/mq/client/notify".equalsIgnoreCase(request.getRequestURI())) {
                String body = getBody(request);
                MsgNotifyRequest request1 = JsonUtil.parseJson(body, MsgNotifyRequest.class);
                MqClientTool.notify(request1);
            }
            if ("/mq/client/traceItem".equalsIgnoreCase(request.getRequestURI())) {
                sbHtml.append(MqClientTool.traceItem(request.getParameter("consumerGroupName"), Long.parseLong(request.getParameter("queueId"))));
            }
            if ("/mq/client/hs".equalsIgnoreCase(request.getRequestURI())) {
                sbHtml.append("OK");
            }
            response.getWriter().write(sbHtml.toString());
            response.flushBuffer();
        } catch (Throwable e) {
        }

    }

    String getBody(HttpServletRequest request) throws IOException {
        BufferedReader br = request.getReader();
        String str, wholeStr = "";
        while ((str = br.readLine()) != null) {
            wholeStr += str;
        }
        return wholeStr;
    }


    @Override
    public void setServer(Server server) {
        // TODO Auto-generated method stub

    }

    @Override
    public Server getServer() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void destroy() {
        // TODO Auto-generated method stub

    }

}
