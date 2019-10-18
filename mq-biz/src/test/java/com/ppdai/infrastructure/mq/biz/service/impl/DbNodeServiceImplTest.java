package com.ppdai.infrastructure.mq.biz.service.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.springframework.test.util.ReflectionTestUtils;

import com.alibaba.druid.pool.DruidDataSource;
import com.ppdai.infrastructure.mq.biz.AbstractTest;
import com.ppdai.infrastructure.mq.biz.dal.meta.DbNodeRepository;
import com.ppdai.infrastructure.mq.biz.entity.DbNodeEntity;
import com.ppdai.infrastructure.mq.biz.entity.LastUpdateEntity;

@RunWith(JUnit4.class)
public class DbNodeServiceImplTest extends AbstractTest {
	private DbNodeRepository dbNodeRepository;
	private DbNodeServiceImpl dbNodeServiceImpl;

	@Before
	public void init() {
		dbNodeServiceImpl = new DbNodeServiceImpl();
		object = dbNodeServiceImpl;
		dbNodeRepository = mockAndSet(DbNodeRepository.class);
		super.init();
		ReflectionTestUtils.setField(object, "soaConfig", soaConfig);
		dbNodeServiceImpl.init();

	}

	@Test
	public void updateDbPropertiesTest() {
		Map<String, DataSource> dbCacheMap = new HashMap<String, DataSource>();
		dbCacheMap.put("test", new DruidDataSource());
		dbNodeServiceImpl.cacheDataMap.set(dbCacheMap);
		setProperty("mq.db.initCount", "11");
		setProperty("mq.db.maxCount", "200");
		dbNodeServiceImpl.updateDbProperties();

		setProperty("mq.db.initCount", "12");
		dbNodeServiceImpl.updateDbProperties();
		setProperty("mq.db.maxCount", "201");
		dbNodeServiceImpl.updateDbProperties();
	}

	@Test
	public void checkChangedTest() {
		assertEquals(true, dbNodeServiceImpl.checkChanged());
		LastUpdateEntity lastUpdateEntity = new LastUpdateEntity();
		lastUpdateEntity.setCount(1);
		lastUpdateEntity.setMaxId(2L);
		when(dbNodeRepository.getLastUpdate()).thenReturn(lastUpdateEntity);
		assertEquals(true, dbNodeServiceImpl.checkChanged());

		lastUpdateEntity = new LastUpdateEntity();
		lastUpdateEntity.setCount(11);
		lastUpdateEntity.setMaxId(2L);
		dbNodeServiceImpl.lastUpdateEntity = lastUpdateEntity;
		assertEquals(true, dbNodeServiceImpl.checkChanged());

		doThrow(new RuntimeException("test")).when(dbNodeRepository).getLastUpdate();
		assertEquals(true, dbNodeServiceImpl.checkChanged());

	}

	@Test
	public void stopTest() {
		dbNodeServiceImpl.stop();
		assertEquals(false, dbNodeServiceImpl.isRunning);
	}

	@Test
	public void checkDataSourceTest() throws SQLException {
		DruidDataSource dataSource = mock(DruidDataSource.class);
		DbNodeServiceImpl.DataSourceFactory dataSourceFactory = new DbNodeServiceImpl.DataSourceFactory() {
			@Override
			public DruidDataSource createDataSource() {
				// TODO Auto-generated method stub
				return dataSource;
			}
		};
		dbNodeServiceImpl.setDataSourceFactory(dataSourceFactory);
		DbNodeEntity dbNodeEntity = new DbNodeEntity();
		boolean flag = false;
		try {
			dbNodeServiceImpl.checkDataSource(dbNodeEntity);
		} catch (Exception e) {
			flag = true;
		}
		assertEquals(false, flag);

		doThrow(new SQLException("tet")).when(dataSource).init();
		flag = false;
		try {
			dbNodeServiceImpl.checkDataSource(dbNodeEntity);
		} catch (Exception e) {
			flag = true;
		}
		assertEquals(true, flag);
	}

	@Test
	public void checkSlaveTest() throws SQLException {
		DruidDataSource dataSource = mock(DruidDataSource.class);
		DbNodeServiceImpl.DataSourceFactory dataSourceFactory = new DbNodeServiceImpl.DataSourceFactory() {
			@Override
			public DruidDataSource createDataSource() {
				// TODO Auto-generated method stub
				return dataSource;
			}
		};
		dbNodeServiceImpl.setDataSourceFactory(dataSourceFactory);
		DbNodeEntity dbNodeEntity = new DbNodeEntity();
		dbNodeEntity.setIpBak("faf");
		dbNodeEntity.setDbUserNameBak("fa");
		dbNodeEntity.setDbPassBak("faf");
		dbNodeEntity.setPortBak(3);
		doThrow(new SQLException("tet")).when(dataSource).init();
		boolean flag = false;
		try {
			dbNodeServiceImpl.checkSlave(dbNodeEntity);
		} catch (Exception e) {
			flag = true;
		}
		assertEquals(true, flag);
		flag = false;
		reset(dataSource);
		try {
			dbNodeServiceImpl.checkSlave(dbNodeEntity);
		} catch (Exception e) {
			flag = true;
		}
		assertEquals(false, flag);
		flag = false;
		dbNodeEntity.setDbPassBak("");
		try {
			dbNodeServiceImpl.checkSlave(dbNodeEntity);
		} catch (Exception e) {
			flag = true;
		}
		assertEquals(false, flag);
	}

	@Test
	public void createDataSourceTest() throws SQLException {
		DruidDataSource dataSource = mock(DruidDataSource.class);
		DbNodeServiceImpl.DataSourceFactory dataSourceFactory = new DbNodeServiceImpl.DataSourceFactory() {
			@Override
			public DruidDataSource createDataSource() {
				// TODO Auto-generated method stub
				return dataSource;
			}
		};
		dbNodeServiceImpl.setDataSourceFactory(dataSourceFactory);
		DbNodeEntity dbNodeEntity = new DbNodeEntity();

		dbNodeEntity.setIp("fasaf");
		dbNodeEntity.setDbUserName("faf");
		dbNodeEntity.setDbPass("aff");
		dbNodeEntity.setPort(2342);

		dbNodeEntity.setIpBak("faf");
		dbNodeEntity.setDbUserNameBak("fa");
		dbNodeEntity.setDbPassBak("faf");
		dbNodeEntity.setPortBak(3);

		dbNodeServiceImpl.createDataSource(dbNodeEntity);
		dbNodeServiceImpl.createDataSource(dbNodeEntity);
		assertEquals(true, dbNodeServiceImpl.cacheDataMap.get().size() == 2);

		doThrow(new SQLException()).when(dataSource).init();
		dbNodeServiceImpl.dbCreated.clear();
		dbNodeServiceImpl.cacheDataMap.set(new HashMap<String, DataSource>());
		dbNodeServiceImpl.createDataSource(dbNodeEntity);

		assertEquals(true, dbNodeServiceImpl.cacheDataMap.get().size() == 0);
	}

	@Test
	public void otherTest() {
		dbNodeServiceImpl.stopPortal();
		dbNodeServiceImpl.startPortal();
		dbNodeServiceImpl.info();
		dbNodeServiceImpl.getCacheJson();
		dbNodeServiceImpl.getDataSource();
		dbNodeServiceImpl.getCacheByIp();
		dbNodeServiceImpl.getCache();
	}

	@Test
	public void getDataSourceTest() {
		//dbNodeServiceImpl.isPortal = true;
		setProperty("mq.db.ip.cat", "1"); 

		Map<Long, DbNodeEntity> data = new HashMap<Long, DbNodeEntity>(); // cacheNodeMap.get();
		DbNodeEntity dbNodeEntity = new DbNodeEntity();
		dbNodeEntity.setIp("fasaf");
		dbNodeEntity.setDbUserName("faf");
		dbNodeEntity.setDbPass("aff");
		dbNodeEntity.setPort(2342);

		dbNodeEntity.setIpBak("faf");
		dbNodeEntity.setDbUserNameBak("fa");
		dbNodeEntity.setDbPassBak("faf");
		dbNodeEntity.setPortBak(3);
		dbNodeEntity.setId(1);
		data.put(1L, dbNodeEntity);
		Map<String, DataSource> datasourceMap = new HashMap<String, DataSource>();
		datasourceMap.put(dbNodeServiceImpl.getConKey(dbNodeEntity, true), new DruidDataSource());
		datasourceMap.put(dbNodeServiceImpl.getConKey(dbNodeEntity, false), new DruidDataSource());
		dbNodeServiceImpl.cacheDataMap.set(datasourceMap);
		dbNodeServiceImpl.cacheNodeMap.set(data);
		assertEquals(true, dbNodeServiceImpl.getDataSource(1, true) != null);
		assertEquals(true, dbNodeServiceImpl.getDataSource(1, false) != null);
	}
	@Test
	public void getDataSource1Test() {
		dbNodeServiceImpl.isPortal = true;
		setProperty("mq.db.ip.cat", "1"); 

		Map<Long, DbNodeEntity> data = new HashMap<Long, DbNodeEntity>(); // cacheNodeMap.get();
		DbNodeEntity dbNodeEntity = new DbNodeEntity();
		dbNodeEntity.setIp("fasaf");
		dbNodeEntity.setDbUserName("faf");
		dbNodeEntity.setDbPass("aff");
		dbNodeEntity.setPort(2342);

		dbNodeEntity.setId(1);
		data.put(1L, dbNodeEntity);
		Map<String, DataSource> datasourceMap = new HashMap<String, DataSource>();
		datasourceMap.put(dbNodeServiceImpl.getConKey(dbNodeEntity, true), new DruidDataSource());
		datasourceMap.put(dbNodeServiceImpl.getConKey(dbNodeEntity, false), new DruidDataSource());
		dbNodeServiceImpl.cacheDataMap.set(datasourceMap);
		dbNodeServiceImpl.cacheNodeMap.set(data);
		assertEquals(true, dbNodeServiceImpl.getDataSource(1, true) != null);
		assertEquals(true, dbNodeServiceImpl.getDataSource(1, false) != null);
	}

	@Test
	public void getDataSource2Test() {
		dbNodeServiceImpl.isPortal = true;
		setProperty("mq.db.ip.cat", "1"); 

		Map<Long, DbNodeEntity> data = new HashMap<Long, DbNodeEntity>(); // cacheNodeMap.get();
		DbNodeEntity dbNodeEntity = new DbNodeEntity();
		dbNodeEntity.setIp("fasaf");
		dbNodeEntity.setDbUserName("faf");
		dbNodeEntity.setDbPass("aff");
		dbNodeEntity.setPort(2342);

		dbNodeEntity.setId(1);
		data.put(1L, dbNodeEntity);
		Map<String, DataSource> datasourceMap = new HashMap<String, DataSource>();
		datasourceMap.put(dbNodeServiceImpl.getConKey(dbNodeEntity, true), new DruidDataSource());
		datasourceMap.put(dbNodeServiceImpl.getConKey(dbNodeEntity, false), new DruidDataSource());	
		assertEquals(true, dbNodeServiceImpl.getDataSource(1, true) == null);
		assertEquals(true, dbNodeServiceImpl.getDataSource(1, false) == null); 
	}

	@Test
	public void getDataSourc3eTest() {
		dbNodeServiceImpl.isPortal = true;
		setProperty("mq.db.ip.cat", "1"); 

		Map<Long, DbNodeEntity> data = new HashMap<Long, DbNodeEntity>(); // cacheNodeMap.get();
		DbNodeEntity dbNodeEntity = new DbNodeEntity();
		dbNodeEntity.setIp("fasaf");
		dbNodeEntity.setDbUserName("faf");
		dbNodeEntity.setDbPass("aff");
		dbNodeEntity.setPort(2342);

		dbNodeEntity.setIpBak("faf");
		dbNodeEntity.setDbUserNameBak("fa");
		dbNodeEntity.setDbPassBak("faf");
		dbNodeEntity.setPortBak(3);
		dbNodeEntity.setId(1);
		data.put(1L, dbNodeEntity);
		Map<String, DataSource> datasourceMap = new HashMap<String, DataSource>();
		datasourceMap.put(dbNodeServiceImpl.getConKey(dbNodeEntity, true), new DruidDataSource());
		datasourceMap.put(dbNodeServiceImpl.getConKey(dbNodeEntity, false)+"2", new DruidDataSource());
		dbNodeServiceImpl.cacheDataMap.set(datasourceMap);
		dbNodeServiceImpl.cacheNodeMap.set(data); 
		assertEquals(true, dbNodeServiceImpl.getDataSource(1, true) != null); 
		assertEquals(true, dbNodeServiceImpl.getDataSource(1, false) != null);
	}
	
	@Test
	public void forceUpdateCacheTest() {
		DruidDataSource dataSource = mock(DruidDataSource.class);
		DbNodeServiceImpl.DataSourceFactory dataSourceFactory = new DbNodeServiceImpl.DataSourceFactory() {
			@Override
			public DruidDataSource createDataSource() {
				// TODO Auto-generated method stub
				return dataSource;
			}
		};
		dbNodeServiceImpl.setDataSourceFactory(dataSourceFactory);
		
		DbNodeEntity dbNodeEntity = new DbNodeEntity();
		dbNodeEntity.setIp("fasaf");
		dbNodeEntity.setDbUserName("faf");
		dbNodeEntity.setDbPass("aff");
		dbNodeEntity.setPort(2342);

		dbNodeEntity.setIpBak("faf");
		dbNodeEntity.setDbUserNameBak("fa");
		dbNodeEntity.setDbPassBak("faf");
		dbNodeEntity.setPortBak(3);
		dbNodeEntity.setId(1);
		
		DbNodeEntity dbNodeEntity1 = new DbNodeEntity();
		dbNodeEntity1.setIp("fasaf");
		dbNodeEntity1.setDbUserName("fa34f");
		dbNodeEntity1.setDbPass("af234f");
		dbNodeEntity1.setPort(2342);

		dbNodeEntity1.setIpBak("faf");
		dbNodeEntity1.setDbUserNameBak("f234a");
		dbNodeEntity1.setDbPassBak("fa234f");
		dbNodeEntity1.setPortBak(3);
		dbNodeEntity1.setId(2);
		
		when(dbNodeRepository.getAll()).thenReturn(Arrays.asList(dbNodeEntity));
		dbNodeServiceImpl.forceUpdateCache();
	}
}
