package com.ppdai.infrastructure.mq.biz.service.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.alibaba.druid.pool.DruidDataSource;
import com.ppdai.infrastructure.mq.biz.AbstractTest;
import com.ppdai.infrastructure.mq.biz.dal.msg.Message01Repository;
import com.ppdai.infrastructure.mq.biz.entity.DbNodeEntity;
import com.ppdai.infrastructure.mq.biz.entity.Message01Entity;
import com.ppdai.infrastructure.mq.biz.entity.TableInfoEntity;
import com.ppdai.infrastructure.mq.biz.service.DbNodeService;

@RunWith(JUnit4.class)
public class Message01ServiceImplTest extends AbstractTest {

	private Message01Repository message01Repository;

	private DbNodeService dbNodeService;

	private Message01ServiceImpl message01ServiceImpl;

	@Before
	public void init() {
		message01ServiceImpl = new Message01ServiceImpl();
		object = message01ServiceImpl;
		message01Repository = mockAndSet(Message01Repository.class);
		dbNodeService = mockAndSet(DbNodeService.class);
		message01ServiceImpl.setDbId(1L);
		super.init();
	}

	@After
	public void clear() {
		message01ServiceImpl.clearDbId();
	}

	@Test
	public void insertBatchDyTest() {
		List<Message01Entity> entities = new ArrayList<Message01Entity>();
		Message01Entity message01Entity = new Message01Entity();
		entities.add(message01Entity);
		message01ServiceImpl.insertBatchDy("test", "test", entities);
		verify(message01Repository).insertBatchDy(anyString(), anyList());

		boolean rs = false;
		doThrow(new RuntimeException("test")).when(message01Repository).insertBatchDy(anyString(), anyList());
		try {
			message01ServiceImpl.insertBatchDy("test", "test", entities);
		} catch (Exception e) {
			rs = true;
		}
		assertEquals(true, rs);
		verify(message01Repository).insertBatchDy(anyString(), anyList());
	}

	@Test
	public void deleteDyTest() {
		message01ServiceImpl.deleteDy("test", 0, 1);
		verify(message01Repository).deleteDy(anyString(), anyLong(), anyLong());

		boolean rs = false;
		doThrow(new RuntimeException("test")).when(message01Repository).deleteDy(anyString(), anyLong(), anyLong());
		try {
			message01ServiceImpl.deleteDy("test", 0, 1);
		} catch (Exception e) {
			rs = true;
		}
		assertEquals(true, rs);
		verify(message01Repository).deleteDy(anyString(), anyLong(), anyLong());
	}

	@Test
	public void getListDyTest() {
		message01ServiceImpl.getListDy("test", "test", 0, 1);
		verify(message01Repository).getListDy(anyString(), anyLong(), anyLong());
		message01ServiceImpl.setDbId(1);
		boolean rs = false;
		doThrow(new RuntimeException("test")).when(message01Repository).getListDy(anyString(), anyLong(), anyLong());
		try {
			message01ServiceImpl.getListDy("test", "test", 0, 1);
		} catch (Exception e) {
			rs = true;
		}
		assertEquals(true, rs);
		verify(message01Repository,times(2)).getListDy(anyString(), anyLong(), anyLong());
	}

	@Test
	public void getListByPageTest() {
		message01ServiceImpl.getListByPage(new HashMap<String, Object>());
		verify(message01Repository).getListByPageSize(anyMap());
		message01ServiceImpl.setDbId(1); 
		boolean rs = false;
		doThrow(new RuntimeException("test")).when(message01Repository).getListByPageSize(anyMap());
		try {
			message01ServiceImpl.getListByPage(new HashMap<String, Object>());
		} catch (Exception e) {
			rs = true;
		}
		assertEquals(true, rs);
		verify(message01Repository,times(2)).getListByPageSize(anyMap());
	}

	@Test
	public void countByPageTest() {
		message01ServiceImpl.countByPage(new HashMap<String, Object>());
		verify(message01Repository).countByPage(anyMap());
		message01ServiceImpl.setDbId(1);
		boolean rs = false;
		doThrow(new RuntimeException("test")).when(message01Repository).countByPage(anyMap());
		try {
			message01ServiceImpl.countByPage(new HashMap<String, Object>());
		} catch (Exception e) {
			rs = true;
		}
		assertEquals(true, rs);
		verify(message01Repository, times(2)).countByPage(anyMap());
	}

	@Test
	public void getMessageByIdTest() {
		message01ServiceImpl.getMessageById("test",1L);
		verify(message01Repository).getMessageById(anyString(), anyLong());
		message01ServiceImpl.setDbId(1);
		boolean rs = false;
		doThrow(new RuntimeException("test")).when(message01Repository).getMessageById(anyString(), anyLong());
		try {
			message01ServiceImpl.getMessageById("test",1L);
		} catch (Exception e) {
			rs = true;
		}
		assertEquals(true, rs);
		verify(message01Repository,times(2)).getMessageById(anyString(), anyLong());
	}

	@Test
	public void getMessageByIdsTest() {
		message01ServiceImpl.getMessageByIds("test", Arrays.asList(1L)); 
		verify(message01Repository).getMessageByIds(anyString(), anyList());
		message01ServiceImpl.setDbId(1);
		boolean rs = false;
		doThrow(new RuntimeException("test")).when(message01Repository).getMessageByIds(anyString(), anyList());
		try {
			message01ServiceImpl.getMessageByIds("test", Arrays.asList(1L)); 
		} catch (Exception e) {
			rs = true;
		}
		assertEquals(true, rs);
		verify(message01Repository,times(2)).getMessageByIds(anyString(), anyList());
	}

	@Test
	public void getTableMinIdTest() {
		message01ServiceImpl.getTableMinId("test");
		verify(message01Repository).getTableMinId(anyString());
	}

	@Test
	public void getMaxIdTest() {
		message01ServiceImpl.getMaxId("test");
		verify(message01Repository).getMaxId(anyString(), anyString());
		message01ServiceImpl.setDbId(1);
		boolean rs = false;
		doThrow(new RuntimeException("test")).when(message01Repository).getMaxId(anyString(), anyString());
		try {
			message01ServiceImpl.getMaxId("test");
		} catch (Exception e) {
			rs = true;
		}
		assertEquals(true, rs);
		verify(message01Repository, times(2)).getMaxId(anyString(), anyString());
	}

	@Test
	public void otherTest() {
		message01ServiceImpl.getDataSource();
		message01ServiceImpl.setDbId(1L);
		message01ServiceImpl.clearDbId();
		message01ServiceImpl.getMaxConnectionsCount();		
		Map<String,String> conMap=new HashMap<String, String>();
		conMap.put("Value", "1");
		when(message01Repository.getMaxConnectionsCount()).thenReturn(conMap);
		assertEquals("1", message01ServiceImpl.getMaxConnectionsCount());
		message01ServiceImpl.getConnectionsCount();
		when(message01Repository.getConnectionsCount()).thenReturn(1);
		assertEquals(1, message01ServiceImpl.getConnectionsCount().intValue());
		
		message01ServiceImpl.setDbId(1L);
		message01ServiceImpl.updateFailMsgResult("test",Arrays.asList(1L),1);
		message01ServiceImpl.setDbId(1L);
		message01ServiceImpl.deleteOldFailMsg("test",1L,1);
		message01ServiceImpl.setDbId(1L);
		message01ServiceImpl.deleteByIds("test",Arrays.asList(1L));		
	}

	@Test
	public void getDbNameTest() {
		assertEquals(null, message01ServiceImpl.getDbName());
		Map<Long, DbNodeEntity> cache = new HashMap<Long, DbNodeEntity>();
		cache.put(1L, new DbNodeEntity());
		cache.get(1L).setDbName("test");
		when(dbNodeService.getCache()).thenReturn(cache);
		message01ServiceImpl.setDbId(1L);
		assertEquals(cache.get(1L).getDbName(), message01ServiceImpl.getDbName());
	}

	@Test
	public void getMaxId1Test() {
		assertEquals(true, message01ServiceImpl.getMaxId().size() == 0);
		message01ServiceImpl.setDbId(1);
		List<TableInfoEntity> dataLst = new ArrayList<TableInfoEntity>();
		TableInfoEntity tableInfoEntity = new TableInfoEntity();
		tableInfoEntity.setAutoIncrement(1L);
		tableInfoEntity.setTableSchema("test");
		tableInfoEntity.setTbName("t1");

		dataLst.add(tableInfoEntity);
		tableInfoEntity = new TableInfoEntity();
		tableInfoEntity.setAutoIncrement(1L);
		tableInfoEntity.setTableSchema("test");
		tableInfoEntity.setTbName("t2");
		dataLst.add(tableInfoEntity);
		when(message01Repository.getMaxIdByDb()).thenReturn(dataLst);
		when(dbNodeService.getDataSource(anyLong(), anyBoolean())).thenReturn(new DruidDataSource());
		assertEquals(true, message01ServiceImpl.getMaxId().size() == 1);

		message01ServiceImpl.setDbId(1);
		doThrow(new RuntimeException()).when(message01Repository).getMaxIdByDb();
		assertEquals(true, message01ServiceImpl.getMaxId().size() == 0);
	}

	@Test
	public void truncateTest() {
		message01ServiceImpl.truncate("test");
		verify(message01Repository).truncate(anyString());
		message01ServiceImpl.setDbId(1L);
		doThrow(new RuntimeException()).when(message01Repository).truncate(anyString());
		boolean rs = false;
		try {
			message01ServiceImpl.truncate("test");
		} catch (Exception e) {
			rs = true;
		}
		assertEquals(true, rs);
	}

	@Test
	public void getTableQuantityByDbNameTest() {
		assertEquals(0, message01ServiceImpl.getTableQuantityByDbName("test"));
		assertEquals(0, message01ServiceImpl.getTableNamesByDbName("test").size());
		
		List<TableInfoEntity> dataLst =new ArrayList<TableInfoEntity>();
		TableInfoEntity tableInfoEntity=new TableInfoEntity();
		tableInfoEntity.setAutoIncrement(1L);
		tableInfoEntity.setTableSchema("test");
		tableInfoEntity.setTbName("test");
		dataLst.add(tableInfoEntity);
		message01ServiceImpl.setDbId(1);		
		when(dbNodeService.getDataSource(anyLong(), anyBoolean())).thenReturn(new DruidDataSource());
		when(message01Repository.getMaxIdByDb()).thenReturn(dataLst);
		assertEquals(1, message01ServiceImpl.getTableNamesByDbName("test").size());
		message01ServiceImpl.setDbId(1);		
		assertEquals(1, message01ServiceImpl.getTableQuantityByDbName("test"));
		
	}

	@Test
	public void createMessageTableTest() {
		message01ServiceImpl.createMessageTable("test");
		verify(message01Repository).createMessageTable(anyString());
	}

	@Test
	public void getListByTimeTest() {
		message01ServiceImpl.getListByTime("test", "test");
		verify(message01Repository).getListByTime(anyString(), anyString());

		boolean rs = false;
		doThrow(new RuntimeException("test")).when(message01Repository).getListByTime(anyString(), anyString());
		try {
			message01ServiceImpl.getListByTime("test", "test");
		} catch (Exception e) {
			rs = true;
		}
		assertEquals(true, rs);
		verify(message01Repository).getListByTime(anyString(), anyString());
	}

	@Test
	public void getNearByMessageByIdTest() {
		message01ServiceImpl.getNearByMessageById("test", 1L);
		verify(message01Repository).getNearByMessageById(anyString(), anyLong());

		boolean rs = false;
		doThrow(new RuntimeException("test")).when(message01Repository).getNearByMessageById(anyString(), anyLong());
		try {
			message01ServiceImpl.getNearByMessageById("test", 1L);
		} catch (Exception e) {
			rs = true;
		}
		assertEquals(true, rs);
		verify(message01Repository).getNearByMessageById(anyString(), anyLong());
	}
	
}
