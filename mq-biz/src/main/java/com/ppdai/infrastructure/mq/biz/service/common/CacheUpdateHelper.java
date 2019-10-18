package com.ppdai.infrastructure.mq.biz.service.common;

import com.ppdai.infrastructure.mq.biz.common.trace.Tracer;
import com.ppdai.infrastructure.mq.biz.common.trace.spi.Transaction;
import com.ppdai.infrastructure.mq.biz.common.util.SpringUtil;
import com.ppdai.infrastructure.mq.biz.service.CacheUpdateService;

import java.util.Map;

/**
 * @Author：wanghe02
 * @Date：2019/4/1 20:02
 */
public class CacheUpdateHelper {
    public static void updateCache() {
        Transaction transaction = Tracer.newTransaction("Portal", "updateCache");
        try {
            Map<String, CacheUpdateService> cacheUpdateServices = SpringUtil.getBeans(CacheUpdateService.class);
            if (cacheUpdateServices != null) {
                cacheUpdateServices.values().forEach(t1 -> {
                    t1.updateCache();
                });
            }
            transaction.setStatus(Transaction.SUCCESS);
        } catch (Exception e) {
            transaction.setStatus(e);
        } finally {
            transaction.complete();
        }
    }
    public static void forceUpdateCache() {
        Transaction transaction = Tracer.newTransaction("Portal", "updateCache");
        try {
            Map<String, CacheUpdateService> cacheUpdateServices = SpringUtil.getBeans(CacheUpdateService.class);
            if (cacheUpdateServices != null) {
                cacheUpdateServices.values().forEach(t1 -> {
                    t1.forceUpdateCache();
                });
            }
            transaction.setStatus(Transaction.SUCCESS);
        } catch (Exception e) {
            transaction.setStatus(e);
        } finally {
            transaction.complete();
        }
    }
}
