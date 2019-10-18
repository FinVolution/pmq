package com.ppdai.infrastructure.mq.biz.service.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.springframework.test.util.ReflectionTestUtils;

import com.ppdai.infrastructure.mq.biz.dal.meta.ConsumerGroupConsumerRepository;

@RunWith(JUnit4.class)
public class ConsumerGroupConsumerServiceImplTest {
	private ConsumerGroupConsumerRepository consumerGroupConsumerRepository;
	private ConsumerGroupConsumerServiceImpl consumerGroupConsumerServiceImpl;

	@Before
	public void init() {
		consumerGroupConsumerServiceImpl = new ConsumerGroupConsumerServiceImpl();
		consumerGroupConsumerRepository = mock(ConsumerGroupConsumerRepository.class);
		ReflectionTestUtils.setField(consumerGroupConsumerServiceImpl, "consumerGroupConsumerRepository",
				consumerGroupConsumerRepository);
		consumerGroupConsumerServiceImpl.init();
	}

	@Test
	public void getByConsumerGroupIdsTest() {
		assertEquals(true, consumerGroupConsumerServiceImpl.getByConsumerGroupIds(null).size() == 0);
		when(consumerGroupConsumerRepository.getByConsumerGroupIds(anyList())).thenReturn(new ArrayList());
		// TODO Auto-generated method stub
		consumerGroupConsumerServiceImpl.getByConsumerGroupIds(Arrays.asList(1L));
		verify(consumerGroupConsumerRepository).getByConsumerGroupIds(anyList());
	}

	@Test
	public void deleteUnActiveConsumerTest() {
		when(consumerGroupConsumerRepository.deleteUnActiveConsumer()).thenReturn(1);
		assertEquals(1, consumerGroupConsumerServiceImpl.deleteUnActiveConsumer());
	}

	@Test
	public void getByConsumerIdsTest() {
		assertEquals(true, consumerGroupConsumerServiceImpl.getByConsumerIds(null).size() == 0);
		when(consumerGroupConsumerRepository.getByConsumerIds(anyList())).thenReturn(new ArrayList());
		// TODO Auto-generated method stub
		consumerGroupConsumerServiceImpl.getByConsumerIds(Arrays.asList(1L));
		verify(consumerGroupConsumerRepository).getByConsumerIds(anyList());
	}

	@Test
	public void deleteByConsumerIdTest() {
		// consumerGroupConsumerRepository.deleteByConsumerId(consumerId);
		doNothing().when(consumerGroupConsumerRepository).deleteByConsumerId(anyLong());
		consumerGroupConsumerServiceImpl.deleteByConsumerId(1L);
		verify(consumerGroupConsumerRepository).deleteByConsumerId(anyLong());
	}

	@Test
	public void deleteByConsumerIdsTest() {
		//consumerGroupConsumerRepository.deleteByConsumerIds(consumerIds);
		doNothing().when(consumerGroupConsumerRepository).deleteByConsumerIds(anyList());
		consumerGroupConsumerServiceImpl.deleteByConsumerIds(Arrays.asList(1L));
		verify(consumerGroupConsumerRepository).deleteByConsumerIds(anyList());
	}

}
