package com.ppdai.infrastructure.mq.biz.dal.meta;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.ppdai.infrastructure.mq.biz.dto.MetaCompareVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;


/**
 * @author dal-generator
 */
@Mapper
public interface DbRepository {

    Date getDbTime();

    Map<String, String> getMaxConnectionsCount();

    Integer getConnectionsCount();

    List<MetaCompareVo> getMetaCompareData(@Param("metaCompareSql") String metaCompareSql);

}
