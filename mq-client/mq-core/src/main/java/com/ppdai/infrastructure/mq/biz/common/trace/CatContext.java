package com.ppdai.infrastructure.mq.biz.common.trace;

import java.util.HashMap;
import java.util.Map;

public class CatContext {
	public static final String ROOT = "_catRootMessageId";
	public static final String PARENT = "_catParentMessageId";
	public static final String CHILD = "_catChildMessageId";
	
	private Map<String,String> properties = new HashMap<String,String>();
   
    public void addProperty(String s, String s1) {
        properties.put(s,s1);
    }
 
    public String getProperty(String s) {
        return properties.get(s);
    }
}
