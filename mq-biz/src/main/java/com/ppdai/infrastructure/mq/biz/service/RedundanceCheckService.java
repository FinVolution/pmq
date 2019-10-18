package com.ppdai.infrastructure.mq.biz.service;
/**
 *冗余字段检查接口
 */
public interface RedundanceCheckService {
	/**
	 *冗余字段检查项
	 */	
	String checkItem();
	/**
	 *冗余字段检查结果
	 */
	String checkResult();
}
