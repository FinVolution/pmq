package com.ppdai.infrastructure.mq.biz.service.common;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.cglib.beans.BeanMap;

import com.ppdai.infrastructure.mq.biz.common.util.JsonUtil;

public class AuditUtil {

    private static final List<String> ignoreFields = Arrays.asList("insertTime", "updateTime", "insertBy", "updateBy");

    public static String diff(Object source, Object target) {
        if (source== null|| target == null) {
            return "";
        }
        if (source.getClass() != target.getClass()){
            throw new RuntimeException("请不要传入不同的数据类型");
        }
        BeanMap sourceBeanMap = BeanMap.create(source);
        String id = JsonUtil.toJson(sourceBeanMap.get("id"));
        Map<String, String> sourceFieldMap = new HashMap<>();
        for (Object key: sourceBeanMap.keySet()) {
            sourceFieldMap.put(key.toString(), JsonUtil.toJson(sourceBeanMap.get(key)));
        }
        BeanMap targetBeanMap = BeanMap.create(target);
        Map<String, String> targetFieldMap = new HashMap<>();
        for (Object key: targetBeanMap.keySet()) {
            targetFieldMap.put(key.toString(), JsonUtil.toJson(targetBeanMap.get(key)));
        }
        StringBuilder result = new StringBuilder();
        for (String key: sourceFieldMap.keySet()) {
            if (!ignoreFields.contains(key)&&!StringUtils.equals(sourceFieldMap.get(key), targetFieldMap.get(key))) {
                result.append(key).append(": {").append(sourceFieldMap.get(key)).append("->").append(targetFieldMap.get(key)).append("}; ");
            }
        }
        if (StringUtils.isEmpty(result)) {
            return "无变动";
        }
        return  "id:[" + id + "]; " + result.toString();
    }

    public static Map<String, String> convertMap(Object source) {
        if (source== null) {
            return new HashMap<>();
        }
        BeanMap sourceBeanMap = BeanMap.create(source);
        Map<String, String> sourceFieldMap = new HashMap<>();
        for (Object key: sourceBeanMap.keySet()) {
            sourceFieldMap.put(key.toString(), JsonUtil.toJson(sourceBeanMap.get(key)));
        }
        return sourceFieldMap;
    }

    public static String diffMap(Map<String, String> sourceFieldMap, Map<String, String> targetBeanMap) {
        String id = sourceFieldMap.get("id");
        Map<String, String> targetFieldMap = new HashMap<>();
        for (Object key: targetBeanMap.keySet()) {
            targetFieldMap.put(key.toString(), JsonUtil.toJson(targetBeanMap.get(key)));
        }
        StringBuilder result = new StringBuilder();
        for (String key: sourceFieldMap.keySet()) {
            if (!ignoreFields.contains(key)&&!StringUtils.equals(sourceFieldMap.get(key), targetFieldMap.get(key))) {
                result.append(key).append(": {").append(sourceFieldMap.get(key)).append("->").append(targetFieldMap.get(key)).append("}; ");
            }
        }
        if (StringUtils.isEmpty(result)) {
            return "无变动";
        }
        return  "id:[" + id + "]; " + result.toString();
    }
}
