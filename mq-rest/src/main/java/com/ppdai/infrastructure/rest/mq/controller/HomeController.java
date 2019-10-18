package com.ppdai.infrastructure.rest.mq.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ppdai.infrastructure.mq.biz.common.util.IPUtil;
import com.ppdai.infrastructure.mq.biz.common.util.SpringUtil;

@Controller
public class HomeController {
	private static final Logger log = LoggerFactory.getLogger(HomeController.class);
	@GetMapping(value = "/", produces = MediaType.TEXT_HTML_VALUE)
	public void home(HttpServletResponse response) {
		response.addHeader("Content-Type", "text/html; charset=UTF-8");
		StringBuilder sbHtml = new StringBuilder();
		sbHtml.append("<!doctype html><html><body>");
		sbHtml.append("当前ip地址为：" + IPUtil.getLocalIP() + "\n");
		sbHtml.append(
				"欢迎使用消息3.0管理中心，需要操作broker和接入，请访问以下地址:<br/><a href='http://fat-mqbroker4.ppdapi.com'>fat-mqbroker4.ppdapi.com</a><br/><a href='http://uat-mqbroker4.ppdapi.com'>uat-mqbroker4.ppdapi.com</a><br/><a href='http://mqbroker4.ppdapi.com'>mqbroker4.ppdapi.com</a><br/>");
		sbHtml.append("</body></html>");
		try {
			response.getWriter().write(sbHtml.toString());
			response.getWriter().close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@GetMapping(value = "/hs", produces = MediaType.TEXT_PLAIN_VALUE)
	public void hs(HttpServletResponse response) {
		//response.addHeader("Content-Type", "text/html; charset=UTF-8");
		StringBuilder sbHtml = new StringBuilder();
		sbHtml.append("OK");
		try {
			response.getWriter().write(sbHtml.toString());
			response.getWriter().close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	@GetMapping(value = "/logTest")
	public String logTest() {
		log.info("Test");		
		log.warn("Test");	
		return "ok";
	}
	
	
	@GetMapping("/mq/getValue")
	@ResponseBody
	public Object getValue(@RequestParam("beanName") String beanName,@RequestParam("fieldName") String fieldName) {
		return SpringUtil.getValue(beanName, fieldName);
	}
}
