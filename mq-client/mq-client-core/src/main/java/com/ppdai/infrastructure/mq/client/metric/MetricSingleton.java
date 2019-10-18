package com.ppdai.infrastructure.mq.client.metric;

import com.codahale.metrics.MetricRegistry;


public class MetricSingleton {    

    private MetricSingleton() {

    }

    /**
     * 懒加载单例帮助类
     */
    private static class SingletonHelper {
    	private static final MetricRegistry INSTANCE = new MetricRegistry();
        //private static final MetricSingleton INSTANCE = new MetricSingleton();
    }

//    /**
//     * 获取DiscoveryClient单例
//     * @return
//     */
//    public static MetricRegistrySingleton getInstance(){
//        return MetricRegistrySingleton.SingletonHelper.INSTANCE;
//    }

    public static MetricRegistry getMetricRegistry() {
        //return MetricSingleton.SingletonHelper.INSTANCE.metricRegistry;
    	return MetricSingleton.SingletonHelper.INSTANCE;
    }
}
