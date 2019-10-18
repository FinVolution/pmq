package com.ppdai.infrastructure.mq.biz.common.trace.internals.cat;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import com.ppdai.infrastructure.mq.biz.common.trace.CatContext;
public class CatContextProxy{
	 //维护一个目标对象
    private CatContext data;
    private Class target;
    public CatContextProxy(Class target,CatContext data){
        this.target=target;
        this.data=data;
    }
   //给目标对象生成代理对象
    public Object getProxyInstance(){
        return Proxy.newProxyInstance(
                Thread.currentThread().getContextClassLoader(),
                new Class[]{target},
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                    	if("addProperty".equals(method.getName())){
                    		data.addProperty(args[0].toString(), args[1].toString());
                    		return null;
                    	}else if("getProperty".equals(method.getName())){
                    		return data.getProperty(args[0].toString());
                    	}
                        return null;
                    }
                }
        );
    }
	}