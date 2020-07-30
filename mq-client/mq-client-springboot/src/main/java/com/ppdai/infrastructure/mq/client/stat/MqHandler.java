package com.ppdai.infrastructure.mq.client.stat;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;

import com.ppdai.infrastructure.mq.client.MqSpringUtil;

@Component
public class MqHandler implements Handler {

	
	private MqClientStatController mqClientStatController;

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
		if (maps.size() == 0) {
			synchronized (this) {
				if (maps.size() == 0) {
					mqClientStatController=MqSpringUtil.getBean(MqClientStatController.class);
					if(mqClientStatController==null) {
						mqClientStatController=new MqClientStatController();
					}
					initMap();
				}
			}
		}
		if (maps.containsKey(request.getRequestURI())) {
			response.setContentType("text/html;charset=UTF-8");
			StringBuilder sbHtml = new StringBuilder();
			sbHtml.append(
					"<!doctype html><html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"></head><body>");

			try {
				sbHtml.append(maps.get(request.getRequestURI()).invoke(mqClientStatController).toString());
				sbHtml.append("</body></html>");
				response.getWriter().write(sbHtml.toString());
				response.flushBuffer();
			} catch (Exception e) {
			}
		}
	}

	private void initMap() {
		Method[] methods = MqClientStatController.class.getMethods();
		for (Method method : methods) {
			GetMapping getMapping = method.getAnnotation(GetMapping.class);
			if (getMapping != null) {
				maps.put(getMapping.value()[0], method);
			}
		}

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
