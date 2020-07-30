package com.ppdai.infrastructure.proxy.util;


import com.codahale.metrics.MetricRegistry;


public class MetricSingleton {

    private MetricRegistry metricRegistry = new MetricRegistry();

    private MetricSingleton() {

    }

    /**
     * 懒加载单例帮助类
     */
    private static class SingletonHelper {
        private static final MetricSingleton INSTANCE = new MetricSingleton();
    }

//    /**
//     * 获取DiscoveryClient单例
//     * @return
//     */
//    public static MetricRegistrySingleton getInstance(){
//        return MetricRegistrySingleton.SingletonHelper.INSTANCE;
//    }

    public static MetricRegistry getMetricRegistry() {
        return MetricSingleton.SingletonHelper.INSTANCE.metricRegistry;
    }
}
