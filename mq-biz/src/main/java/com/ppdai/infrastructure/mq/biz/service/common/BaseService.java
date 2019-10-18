package com.ppdai.infrastructure.mq.biz.service.common;

import java.util.List;
import java.util.Map;

/**
 * @author dal-generator
 */
public interface BaseService<T> {

    long insert(T t);

    void insertBatch(List<T> entityList);

    T get(long id);

    int update(T t);

    long count(Map<String, Object> conditionMap);

    long countPage(Map<String, Object> conditionMap, long pageSize);
    
    List<T> getList();

    List<T> getList(Map<String, Object> conditionMap);

    List<T> getList(Map<String, Object> conditionMap, long page, long pageSize);
    
    T get(Map<String, Object> conditionMap);

    List<T> getList(List<Long> ids);
    
    void delete(long id);

    void delete(List<Long> ids);
}
