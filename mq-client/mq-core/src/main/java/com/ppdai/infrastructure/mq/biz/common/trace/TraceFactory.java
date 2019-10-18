package com.ppdai.infrastructure.mq.biz.common.trace;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TraceFactory {
	private static Map<String, TraceMessage> traces = new ConcurrentHashMap<>();
	private static Object lockobj = new Object();
	private static volatile TraceCheck traceCheck;	
	public static void setTraceCheck(TraceCheck traceCheck1){
		traceCheck=traceCheck1;		
	}
	public static boolean isEnabled(String name) {
		if(traceCheck!=null){
			try{
			return traceCheck.isEnabled(name);
			}catch (Exception e) {
				return true;
			}
		}
		return true;		
	}
	public static void remove(String name) {
		traces.remove(name);
	}
	public static TraceMessage getInstance(String name) {
		if (!traces.containsKey(name)) {
			synchronized (lockobj) {
				if (!traces.containsKey(name)) {
					traces.put(name, new TraceMessage(name));
				}
			}
		}
		return traces.get(name);

	}

	// 获取指定name的trace信息，如果不存在返回空
	public static TraceMessage getTrace(String name) {
		TraceMessage trace = null;
		if (traces.containsKey(name)) {
			trace = traces.get(name);
		}
		return trace;
	}
	// 获取所有的trace信息
	public static Map<String, TraceMessage> getTraces() {
		Map<String, TraceMessage> traces1=new HashMap<>();
		traces.entrySet().forEach(t1->{
			if(t1.getValue().getData().size()>0){
				traces1.put(t1.getKey(), t1.getValue());
			}
		});
		return traces1;
	}
	
	public interface TraceCheck{
		boolean isEnabled(String name);
		
	}
}
