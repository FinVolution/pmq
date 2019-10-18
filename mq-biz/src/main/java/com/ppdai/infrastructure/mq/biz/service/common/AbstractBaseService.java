package com.ppdai.infrastructure.mq.biz.service.common;

import com.ppdai.infrastructure.mq.biz.dal.common.BaseRepository;

import java.util.List;
import java.util.Map;

/**
 * @author dal-generator
 */
public abstract class AbstractBaseService<T> implements BaseService<T> {

    private BaseRepository<T> baseRepository;

    protected void setBaseRepository(BaseRepository<T> baseRepository) {
        this.baseRepository = baseRepository;
    }

    @Override
    public long insert(T t) {
        return baseRepository.insert(t);
    }

    @Override
    public void insertBatch(List<T> entityList) {
        if (entityList != null && entityList.size() > 0) {
            baseRepository.insertBatch(entityList);
        }
    }

    @Override
    public T get(long id) {
        return baseRepository.getById(id);
    }

    @Override
    public int update(T t) {
        return baseRepository.update(t);
    }

    @Override
    public long count(Map<String, Object> conditionMap) {
        return baseRepository.count(conditionMap);
    }
    
    @Override
    public long countPage(Map<String, Object> conditionMap, long pageSize) {
        return (baseRepository.count(conditionMap) + pageSize -1)/pageSize;
    }

    @Override
    public List<T> getList() {
        return baseRepository.getAll();
    }

    @Override
    public List<T> getList(Map<String, Object> conditionMap) {
        return baseRepository.getList(conditionMap);
    }

    @Override
    public List<T> getList(Map<String, Object> conditionMap, long page, long pageSize) {
        conditionMap.put("start1", (page - 1) * pageSize);
        conditionMap.put("offset1", pageSize);
        return baseRepository.getListByPage(conditionMap);    
    }
    
    @Override
    public T get(Map<String, Object> conditionMap) {
        return baseRepository.get(conditionMap);
    }
    
    @Override
    public List<T> getList(List<Long> ids) {
        return baseRepository.getByIds(ids);
    }
    
    @Override
    public void delete(long id) {
        baseRepository.delete(id);
    }
    
    @Override
    public void delete(List<Long> ids) {
        baseRepository.batchDelete(ids);
    }
}
