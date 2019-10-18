package com.ppdai.infrastructure.ui.controller;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.ppdai.infrastructure.mq.biz.common.trace.TraceFactory;
import com.ppdai.infrastructure.mq.biz.common.util.HttpClient;
import com.ppdai.infrastructure.mq.biz.common.util.SpringUtil;
import com.ppdai.infrastructure.mq.biz.common.util.Util;
import com.ppdai.infrastructure.mq.biz.service.CacheUpdateService;

@RestController
public class ToolController {
	private static final Logger log = LoggerFactory.getLogger(ToolController.class);
	HttpClient httpClient = new HttpClient();

	@GetMapping("/mq/client/monitor")
	public String getMonitor(@RequestParam(name = "path") String path,@RequestParam(name = "hostPort") String hostPort,
							 String consumerGroupName, long queueId){
		String ip=hostPort.substring(0,hostPort.indexOf("|"));
		String port=hostPort.substring(hostPort.lastIndexOf("|")+1);
		String url="http://"+ip+":"+port+"/mq/client/"+path;
		String traceItemUrl="http://"+ip+":"+port+"/mq/client/traceItem?consumerGroupName="+consumerGroupName+"&queueId="+queueId;

		if("trace".equals(path)){
			return httpPost(traceItemUrl);
		}else{
			return httpPost(url);
		}
	}


	private String httpPost(String url){
		String responseData="";
		try {
			responseData=httpClient.get(url);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return responseData;
	}

	@GetMapping("/cacheJson")
	public String cacheJson(@RequestParam("key") String key) {
		if (Util.isEmpty(key))
			return "";
		Map<String, CacheUpdateService> cacheUpdateMap = SpringUtil.getBeans(CacheUpdateService.class);
		StringBuilder rs = new StringBuilder(10000);
		cacheUpdateMap.entrySet().forEach(t1 -> {
			if (t1.getKey().toLowerCase().equals(key.toLowerCase())) {
				rs.append(t1.getValue().getCacheJson());
			}
		});
		return rs.toString();
	}

	@GetMapping(value = "/hs", produces = MediaType.TEXT_HTML_VALUE)
	public void hs(HttpServletResponse response) {
		// messageCleanService.doStart();
		response.addHeader("Content-Type", "text/html; charset=UTF-8");
		StringBuilder sbHtml = new StringBuilder();
		sbHtml.append("<!doctype html><html><body>OK</body></html>");
		try {
			response.getWriter().write(sbHtml.toString());
			response.getWriter().close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@GetMapping("/mq/trace")
	@ResponseBody
	public Object getTrace() {
		return TraceFactory.getTraces();
	}

	@GetMapping("/mq/getValue")
	@ResponseBody
	public Object getValue(@RequestParam("beanName") String beanName, @RequestParam("fieldName") String fieldName) {
		return SpringUtil.getValue(beanName, fieldName);
	}
}
