package com.ppdai.infrastructure.mq.biz.dal.common;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author dal-generator
 */
public interface BaseRepository<T> {

    long insert(T t);
    
    void insertBatch(@Param("entityList") List<T> tList);
    
    T getById(@Param("id") long id);
        
    T get(Map<String, Object> conditionMap);
    
    List<T> getByIds(@Param("ids") List<Long> ids);
    
    int update(T t);
    
    long count(Map<String, Object> conditionMap);
    
    List<T> getList(Map<String, Object> conditionMap);
    
    List<T> getAll();
    
    List<T> getListByPage(Map<String, Object> conditionMap);
    
    void delete(long id);
    
    void batchDelete(@Param("ids") List<Long> ids);

}

