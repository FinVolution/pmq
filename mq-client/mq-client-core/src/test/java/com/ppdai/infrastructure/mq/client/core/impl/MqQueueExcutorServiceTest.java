//package com.ppdai.infrastructure.mq.client.core.impl;
//
//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertNotEquals;
//
//import java.lang.reflect.Field;
//import java.lang.reflect.InvocationTargetException;
////import java.lang.reflect.Method;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.concurrent.BlockingQueue;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//import java.util.concurrent.atomic.AtomicBoolean;
//import java.util.concurrent.atomic.AtomicReference;
//
//import org.apache.commons.lang.reflect.FieldUtils;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.junit.runners.JUnit4;
//
//import com.ppdai.infrastructure.mq.biz.common.util.Util;
//import com.ppdai.infrastructure.mq.biz.dto.base.ConsumerQueueDto;
//import com.ppdai.infrastructure.mq.biz.dto.base.MessageDto;
//import com.ppdai.infrastructure.mq.biz.dto.base.ProducerDataDto;
//import com.ppdai.infrastructure.mq.biz.dto.client.CommitOffsetRequest;
//import com.ppdai.infrastructure.mq.biz.dto.client.ConsumerGroupOneDto;
//import com.ppdai.infrastructure.mq.biz.dto.client.FailMsgPublishAndUpdateResultRequest;
//import com.ppdai.infrastructure.mq.biz.dto.client.LogRequest;
//import com.ppdai.infrastructure.mq.biz.dto.client.PullDataRequest;
//import com.ppdai.infrastructure.mq.biz.dto.client.PullDataResponse;
//import com.ppdai.infrastructure.mq.biz.dto.client.SendMailRequest;
//import com.ppdai.infrastructure.mq.biz.event.IPartitionSelector;
//import com.ppdai.infrastructure.mq.biz.event.ISubscriber;
//import com.ppdai.infrastructure.mq.biz.event.ISubscriberSelector;
//import com.ppdai.infrastructure.mq.biz.event.PostHandleListener;
//import com.ppdai.infrastructure.mq.biz.event.PreHandleListener;
//import com.ppdai.infrastructure.mq.client.AbstractMockMqClientBase;
//import com.ppdai.infrastructure.mq.client.AbstractMockResource;
//import com.ppdai.infrastructure.mq.client.AbstractTest;
//import com.ppdai.infrastructure.mq.client.MqContext;
//import com.ppdai.infrastructure.mq.client.exception.ContentExceed65535Exception;
//import com.ppdai.infrastructure.mq.client.exception.MqNotInitException;
//
//@RunWith(JUnit4.class)
//public class MqQueueExcutorServiceTest extends AbstractTest {
//
//	@Test
//	public void testConstruct() throws IllegalArgumentException, IllegalAccessException {
//		MockMqClientBase mockMqClientBase = new MockMqClientBase();
//		MqQueueExcutorService mqQueueExcutorService = new MqQueueExcutorService(mockMqClientBase, consumerGroupName,
//				buildDefaultConsumerQueueDto());
//		Field f = FieldUtils.getDeclaredField(MqQueueExcutorService.class, "executor", true);
//		assertNotEquals("executor construct error", null, f.get(mqQueueExcutorService));
//
//		f = FieldUtils.getDeclaredField(MqQueueExcutorService.class, "iSubscriber", true);
//		assertNotEquals("iSubscriber construct error", null, f.get(mqQueueExcutorService));
//
//		f = FieldUtils.getDeclaredField(MqQueueExcutorService.class, "mqResource", true);
//		assertNotEquals("mqResource construct error", null, f.get(mqQueueExcutorService));
//	}
//
//	@Test
//	public void testThreadSize() throws IllegalArgumentException, IllegalAccessException {
//		MockMqClientBase mockMqClientBase = new MockMqClientBase();
//		MqQueueExcutorService mqQueueExcutorService = new MqQueueExcutorService(mockMqClientBase, consumerGroupName,
//				buildDefaultConsumerQueueDto());
//		ConsumerQueueDto consumerQueueDto = buildDefaultConsumerQueueDto();
//		consumerQueueDto.setThreadSize(consumerQueueDto.getThreadSize() + 1);
//		mqQueueExcutorService.updateQueueMeta(consumerQueueDto);
//		Field f = FieldUtils.getDeclaredField(MqQueueExcutorService.class, "consumerQueueRef", true);
//		@SuppressWarnings("unchecked")
//		AtomicReference<ConsumerQueueDto> consumerQueueRef = (AtomicReference<ConsumerQueueDto>) (f
//				.get(mqQueueExcutorService));
//		assertEquals("threadSize error", consumerQueueDto.getThreadSize(), consumerQueueRef.get().getThreadSize());
//	}
//
//	@Test
//	public void testQueueOffsetVersion() throws IllegalArgumentException, IllegalAccessException {
//		MockMqClientBase mockMqClientBase = new MockMqClientBase();
//		MqQueueExcutorService mqQueueExcutorService = new MqQueueExcutorService(mockMqClientBase, consumerGroupName,
//				buildDefaultConsumerQueueDto());
//		ConsumerQueueDto consumerQueueDto = buildDefaultConsumerQueueDto();
//		consumerQueueDto.setOffsetVersion(consumerQueueDto.getOffsetVersion() + 1);
//		mqQueueExcutorService.updateQueueMeta(consumerQueueDto);
//		Field f = FieldUtils.getDeclaredField(MqQueueExcutorService.class, "consumerQueueRef", true);
//		@SuppressWarnings("unchecked")
//		AtomicReference<ConsumerQueueDto> consumerQueueRef = (AtomicReference<ConsumerQueueDto>) (f
//				.get(mqQueueExcutorService));
//		assertEquals("OffsetVersion error", consumerQueueDto.getOffset(), consumerQueueRef.get().getLastId());
//	}
//
//	@Test
//	public void testStop() throws IllegalArgumentException, IllegalAccessException {
//		MockMqClientBase mockMqClientBase = new MockMqClientBase();
//		MqQueueExcutorService mqQueueExcutorService = new MqQueueExcutorService(mockMqClientBase, consumerGroupName,
//				buildDefaultConsumerQueueDto());
//
//		MqQueueResource resource = (MqQueueResource) mockMqClientBase.getContext().getMqResource();
//		ConsumerQueueDto consumerQueueDto = buildDefaultConsumerQueueDto();
//		consumerQueueDto.setStopFlag(0);
//		mqQueueExcutorService.updateQueueMeta(consumerQueueDto);
//		assertEquals("stop error", 0, resource.getCommitFlag());
//		consumerQueueDto.setStopFlag(1);
//		mqQueueExcutorService.updateQueueMeta(consumerQueueDto);
//		assertEquals("stop error", 0, resource.getCommitFlag());
//	}
//
//	@Test
//	public void testOffsetVersionChangedAndStop() throws IllegalArgumentException, IllegalAccessException {
//		MockMqClientBase mockMqClientBase = new MockMqClientBase();
//		MqQueueExcutorService mqQueueExcutorService = new MqQueueExcutorService(mockMqClientBase, consumerGroupName,
//				buildDefaultConsumerQueueDto());
//
//		MqQueueResource resource = (MqQueueResource) mockMqClientBase.getContext().getMqResource();
//		ConsumerQueueDto consumerQueueDto = buildDefaultConsumerQueueDto();
//		consumerQueueDto.setStopFlag(0);
//		mqQueueExcutorService.updateQueueMeta(consumerQueueDto);
//		assertEquals("stop error", 0, resource.getCommitFlag());
//		consumerQueueDto.setStopFlag(1);
//		consumerQueueDto.setOffsetVersion(consumerQueueDto.getOffsetVersion() + 1);
//		mqQueueExcutorService.updateQueueMeta(consumerQueueDto);
//		assertEquals("stop error", 0, resource.getCommitFlag());
//	}
//
//	@Test
//	public void testDoPullingDataNotFull() throws IllegalArgumentException, IllegalAccessException,
//			NoSuchMethodException, SecurityException, InvocationTargetException {
//		MockMqClientBase mockMqClientBase = new MockMqClientBase();
//		MqQueueExcutorService mqQueueExcutorService = new MqQueueExcutorService(mockMqClientBase, consumerGroupName,
//				buildDefaultConsumerQueueDto());
//
//		// MqQueueResource resource = (MqQueueResource)
//		// mockMqClientBase.getContext().getMqResource();
//		ConsumerQueueDto consumerQueueDto = buildDefaultConsumerQueueDto();
//		consumerQueueDto.setStopFlag(0);
//		mqQueueExcutorService.updateQueueMeta(consumerQueueDto);
//
////		Method doPullingData = MqQueueExcutorService.class.getDeclaredMethod("doPullingData");
////		doPullingData.setAccessible(true);
////		doPullingData.invoke(mqQueueExcutorService);
//		mqQueueExcutorService.doPullingData();
//
//		Field f = FieldUtils.getDeclaredField(MqQueueExcutorService.class, "messages", true);
//		@SuppressWarnings("unchecked")
//		BlockingQueue<MessageDto> messages = (BlockingQueue<MessageDto>) (f.get(mqQueueExcutorService));
//		assertEquals("testDoPullingDataNotFull 1 error", 1, messages.size());
//
//		// doPullingData.invoke(mqQueueExcutorService);
//		mqQueueExcutorService.doPullingData();
//		assertEquals("testDoPullingDataNotFull 2 error", 2, messages.size());
//	}
//
//	@Test
//	public void testDoPullingDataFull() throws IllegalArgumentException, IllegalAccessException, NoSuchMethodException,
//			SecurityException, InvocationTargetException {
//		MockMqClientBase mockMqClientBase = new MockMqClientBase();
//		MqQueueExcutorService mqQueueExcutorService = new MqQueueExcutorService(mockMqClientBase, consumerGroupName,
//				buildDefaultConsumerQueueDto());
//
//		// MqQueueResource resource = (MqQueueResource)
//		// mockMqClientBase.getContext().getMqResource();
//		ConsumerQueueDto consumerQueueDto = buildDefaultConsumerQueueDto();
//		consumerQueueDto.setStopFlag(0);
//		consumerQueueDto.setPullBatchSize(301);
//		mqQueueExcutorService.updateQueueMeta(consumerQueueDto);
//
////		Method doPullingData = MqQueueExcutorService.class.getDeclaredMethod("doPullingData");
////		doPullingData.setAccessible(true);
//		Runnable runnable = new Runnable() {
//			@Override
//			public void run() {
////					doPullingData.invoke(mqQueueExcutorService);
//				mqQueueExcutorService.doPullingData();
//
//			}
//		};
//
//		ExecutorService executorService = Executors.newSingleThreadExecutor();
//		executorService.submit(runnable);
//		Util.sleep(2000);
//		Field f = FieldUtils.getDeclaredField(MqQueueExcutorService.class, "messages", true);
//		@SuppressWarnings("unchecked")
//		BlockingQueue<MessageDto> messages = (BlockingQueue<MessageDto>) (f.get(mqQueueExcutorService));
//		assertEquals("testDoPullingDataFull 300 error", 300, messages.size());
//		for (int i = 0; i < 300; i++) {
//			messages.poll();
//		}
//		Util.sleep(2000);
//		assertEquals("testDoPullingDataFull 1 error", 1, messages.size());
//	}
//
//	@Test
//	public void testCheckPreHandFales() throws NoSuchMethodException, SecurityException, IllegalAccessException,
//			IllegalArgumentException, InvocationTargetException {
//		MockMqClientBase mockMqClientBase = new MockMqClientBase();
//		mockMqClientBase.getContext().getMqEvent().setPreHandleListener(new PreHandleListener() {
//			@Override
//			public boolean preHandle(ConsumerQueueDto consumerQueue) {
//				// TODO Auto-generated method stub
//				return false;
//			}
//		});
//
//		ConsumerQueueDto consumerQueueDto = buildDefaultConsumerQueueDto();
//		MqQueueExcutorService mqQueueExcutorService = new MqQueueExcutorService(mockMqClientBase, consumerGroupName,
//				consumerQueueDto);
//		boolean rs = mqQueueExcutorService.checkPreHand(consumerQueueDto);
//		assertEquals("testCheckPreHand error", false, rs);
//	}
//
//	@Test
//	public void testCheckPreHandTrue() throws NoSuchMethodException, SecurityException, IllegalAccessException,
//			IllegalArgumentException, InvocationTargetException {
//		MockMqClientBase mockMqClientBase = new MockMqClientBase();
//		mockMqClientBase.getContext().getMqEvent().setPreHandleListener(new PreHandleListener() {
//			@Override
//			public boolean preHandle(ConsumerQueueDto consumerQueue) {
//				return true;
//			}
//		});
//
//		ConsumerQueueDto consumerQueueDto = buildDefaultConsumerQueueDto();
//		MqQueueExcutorService mqQueueExcutorService = new MqQueueExcutorService(mockMqClientBase, consumerGroupName,
//				consumerQueueDto);
//		boolean rs = mqQueueExcutorService.checkPreHand(consumerQueueDto);
//		assertEquals("testCheckPreHand error", true, rs);
//	}
//
//	@Test
//	public void testCheckPreHandException() throws NoSuchMethodException, SecurityException, IllegalAccessException,
//			IllegalArgumentException, InvocationTargetException {
//		MockMqClientBase mockMqClientBase = new MockMqClientBase();
//		mockMqClientBase.getContext().getMqEvent().setPreHandleListener(new PreHandleListener() {
//			@Override
//			public boolean preHandle(ConsumerQueueDto consumerQueue) {
//				// TODO Auto-generated method stub
//				throw new RuntimeException();
//			}
//		});
//
//		ConsumerQueueDto consumerQueueDto = buildDefaultConsumerQueueDto();
//		MqQueueExcutorService mqQueueExcutorService = new MqQueueExcutorService(mockMqClientBase, consumerGroupName,
//				consumerQueueDto);
//		boolean rs = mqQueueExcutorService.checkPreHand(consumerQueueDto);
//		assertEquals("testCheckPreHand error", false, rs);
//	}
//
//	@Test
//	public void testHandleDataVersionNotSame() throws NoSuchMethodException, SecurityException,
//			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
//		MockMqClientBase mockMqClientBase = new MockMqClientBase();
//		ConsumerQueueDto consumerQueueDto = buildDefaultConsumerQueueDto();
//		consumerQueueDto.setPullBatchSize(10);
//		MqQueueExcutorService mqQueueExcutorService = new MqQueueExcutorService(mockMqClientBase, consumerGroupName,
//				consumerQueueDto);
//		mqQueueExcutorService.updateQueueMeta(consumerQueueDto);
//		mqQueueExcutorService.doPullingData();
//		mqQueueExcutorService.handleData();
//		Util.sleep(3000);
//		assertEquals("testDoHandleDataVersionNotSame error", 0, mockMqClientBase.getMqQueueSub().getCount());
//	}
//
//	@Test
//	public void testHandleDataVersionStop() throws NoSuchMethodException, SecurityException, IllegalAccessException,
//			IllegalArgumentException, InvocationTargetException {
//		MockMqClientBase mockMqClientBase = new MockMqClientBase();
//		ConsumerQueueDto consumerQueueDto = buildDefaultConsumerQueueDto();
//		consumerQueueDto.setStopFlag(1);
//		consumerQueueDto.setPullBatchSize(10);
//		MqQueueExcutorService mqQueueExcutorService = new MqQueueExcutorService(mockMqClientBase, consumerGroupName,
//				consumerQueueDto);
//		mqQueueExcutorService.updateQueueMeta(consumerQueueDto);
//		mqQueueExcutorService.doPullingData();
//		mqQueueExcutorService.handleData();
//		Util.sleep(3000);
//		assertEquals("testDoHandleDataVersionStop error", 0, mockMqClientBase.getMqQueueSub().getCount());
//	}
//
//	@Test
//	public void testHandleDataVersionSameDealSuc() throws NoSuchMethodException, SecurityException,
//			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
//		MockMqClientBase mockMqClientBase = new MockMqClientBase();
//		ConsumerQueueDto consumerQueueDto = buildDefaultConsumerQueueDto();
//		consumerQueueDto.setPullBatchSize(100);
//		consumerQueueDto.setTraceFlag(1);
//		MqQueueExcutorService mqQueueExcutorService = new MqQueueExcutorService(mockMqClientBase, consumerGroupName,
//				consumerQueueDto);
//		mqQueueExcutorService.updateQueueMeta(consumerQueueDto);
//
//		mqQueueExcutorService.doPullingData();
//		Util.sleep(1000);
//		consumerQueueDto.setTag("");
//		consumerQueueDto.setTimeout(0);
//		consumerQueueDto.setTraceFlag(1);
//		for (int i = 0; i < 30; i++) {
//			mqQueueExcutorService.handleData();
//		}
//		Util.sleep(5000);
//		assertEquals("testDoHandleDataVersionNotSame error", 100, mockMqClientBase.getMqQueueSub().getCount());
//		assertEquals("testDoHandleDataVersionNotSame AddLog error", true,
//				((MqQueueResource) mockMqClientBase.getContext().getMqResource()).getAddLogFlag() > 0);
//
//		assertEquals("testDoHandleDataVersionNotSame PublishAndUpdateResultFail error", 0,
//				((MqQueueResource) mockMqClientBase.getContext().getMqResource())
//						.getPublishAndUpdateResultFailMsgFlag());
//	}
//
//	@Test
//	public void testHandleDataVersionSameDealSucPost() throws NoSuchMethodException, SecurityException,
//			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
//		MockMqClientBase mockMqClientBase = new MockMqClientBase();
//		AtomicBoolean flag=new AtomicBoolean(false);
//		mockMqClientBase.getContext().getMqEvent().setPostHandleListener(new PostHandleListener() {
//			@Override
//			public void postHandle(ConsumerQueueDto consumerQueue, Boolean isSuc) {
//				flag.set(true);
//			}
//		});
//		ConsumerQueueDto consumerQueueDto = buildDefaultConsumerQueueDto();
//		consumerQueueDto.setPullBatchSize(10);
//		consumerQueueDto.setTraceFlag(1);
//		MqQueueExcutorService mqQueueExcutorService = new MqQueueExcutorService(mockMqClientBase, consumerGroupName,
//				consumerQueueDto);
//		mqQueueExcutorService.updateQueueMeta(consumerQueueDto);
//
//		mqQueueExcutorService.doPullingData();
//		Util.sleep(1000);
//		consumerQueueDto.setTag("");
//		consumerQueueDto.setTimeout(0);
//		consumerQueueDto.setTraceFlag(1);
//		mqQueueExcutorService.handleData();
//		assertEquals("testDoHandleDataVersionSameDealSucPost error", true,flag.get());
//	}
//
//	@Test
//	public void testHandleDataVersionSameDealFail() throws NoSuchMethodException, SecurityException,
//			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
//		MockMqClientBase mockMqClientBase = new MockMqClientBase();
//		ConsumerQueueDto consumerQueueDto = buildDefaultConsumerQueueDto();
//		consumerQueueDto.setPullBatchSize(100);
//		consumerQueueDto.setTraceFlag(1);
//		mockMqClientBase.getMqQueueSub().setFail(true);
//		MqQueueExcutorService mqQueueExcutorService = new MqQueueExcutorService(mockMqClientBase, consumerGroupName,
//				consumerQueueDto);
//		mqQueueExcutorService.updateQueueMeta(consumerQueueDto);
//		mqQueueExcutorService.doPullingData();
//		Util.sleep(1000);
//		consumerQueueDto.setTag("");
//		consumerQueueDto.setTimeout(0);
//		consumerQueueDto.setTraceFlag(1);
//		for (int i = 0; i < 20; i++) {
//			mqQueueExcutorService.handleData();
//		}
//		Util.sleep(10000); 
//		assertEquals("testDoHandleDataVersionNotSame error", 100, mockMqClientBase.getMqQueueSub().getCount());
//		assertEquals("testDoHandleDataVersionNotSame PublishAndUpdateResultFail error", 100,
//				((MqQueueResource) mockMqClientBase.getContext().getMqResource())
//						.getPublishAndUpdateResultFailMsgFlag());
//	}
//
//	@Test
//	public void testUpdateOffsetIdLess() throws NoSuchMethodException, SecurityException, IllegalAccessException,
//			IllegalArgumentException, InvocationTargetException {
//
//		MockMqClientBase mockMqClientBase = new MockMqClientBase();
//		ConsumerQueueDto consumerQueueDto = buildDefaultConsumerQueueDto();
//		consumerQueueDto.setPullBatchSize(100);
//		MqQueueExcutorService mqQueueExcutorService = new MqQueueExcutorService(mockMqClientBase, consumerGroupName,
//				consumerQueueDto);
//		mqQueueExcutorService.updateQueueMeta(consumerQueueDto);
//
//		consumerQueueDto = buildDefaultConsumerQueueDto();
//		mqQueueExcutorService.updateOffset(consumerQueueDto, consumerQueueDto.getOffset() - 1);
//		Field f = FieldUtils.getDeclaredField(MqQueueExcutorService.class, "consumerQueueRef", true);
//		@SuppressWarnings("unchecked")
//		AtomicReference<ConsumerQueueDto> consumerQueueRef = (AtomicReference<ConsumerQueueDto>) (f
//				.get(mqQueueExcutorService));
//		assertEquals("testUpdateOffsetIdLess error", consumerQueueRef.get().getOffset(), consumerQueueDto.getOffset());
//	}
//
//	@Test
//	public void testUpdateOffsetIdMore() throws NoSuchMethodException, SecurityException, IllegalAccessException,
//			IllegalArgumentException, InvocationTargetException {
//
//		MockMqClientBase mockMqClientBase = new MockMqClientBase();
//		ConsumerQueueDto consumerQueueDto = buildDefaultConsumerQueueDto();
//		consumerQueueDto.setPullBatchSize(100);
//		MqQueueExcutorService mqQueueExcutorService = new MqQueueExcutorService(mockMqClientBase, consumerGroupName,
//				consumerQueueDto);
//		mqQueueExcutorService.updateQueueMeta(consumerQueueDto);
//
//		consumerQueueDto = buildDefaultConsumerQueueDto();
//		mqQueueExcutorService.updateOffset(consumerQueueDto, consumerQueueDto.getOffset() + 1);
//		Field f = FieldUtils.getDeclaredField(MqQueueExcutorService.class, "consumerQueueRef", true);
//		@SuppressWarnings("unchecked")
//		AtomicReference<ConsumerQueueDto> consumerQueueRef = (AtomicReference<ConsumerQueueDto>) (f
//				.get(mqQueueExcutorService));
//		assertEquals("testUpdateOffsetIdLess error", consumerQueueRef.get().getOffset() + 1,
//				consumerQueueDto.getOffset());
//	}
//
//	@Test
//	public void testUpdateLastIdLess() throws NoSuchMethodException, SecurityException, IllegalAccessException,
//			IllegalArgumentException, InvocationTargetException {
//
//		MockMqClientBase mockMqClientBase = new MockMqClientBase();
//		ConsumerQueueDto consumerQueueDto = buildDefaultConsumerQueueDto();
//		consumerQueueDto.setPullBatchSize(100);
//		MqQueueExcutorService mqQueueExcutorService = new MqQueueExcutorService(mockMqClientBase, consumerGroupName,
//				consumerQueueDto);
//		mqQueueExcutorService.updateQueueMeta(consumerQueueDto);
//
//		consumerQueueDto = buildDefaultConsumerQueueDto();
//		MessageDto messageDto = new MessageDto();
//		messageDto.setId(consumerQueueDto.getLastId() - 1);
//		mqQueueExcutorService.updateLastId(consumerQueueDto, messageDto);
//		Field f = FieldUtils.getDeclaredField(MqQueueExcutorService.class, "consumerQueueRef", true);
//		@SuppressWarnings("unchecked")
//		AtomicReference<ConsumerQueueDto> consumerQueueRef = (AtomicReference<ConsumerQueueDto>) (f
//				.get(mqQueueExcutorService));
//		assertEquals("testUpdateLastIdLess error", consumerQueueRef.get().getLastId(), consumerQueueDto.getOffset());
//	}
//
//	@Test
//	public void testUpdateLastIdMore() throws NoSuchMethodException, SecurityException, IllegalAccessException,
//			IllegalArgumentException, InvocationTargetException {
//		MockMqClientBase mockMqClientBase = new MockMqClientBase();
//		ConsumerQueueDto consumerQueueDto = buildDefaultConsumerQueueDto();
//		consumerQueueDto.setPullBatchSize(100);
//		MqQueueExcutorService mqQueueExcutorService = new MqQueueExcutorService(mockMqClientBase, consumerGroupName,
//				consumerQueueDto);
//
//		mqQueueExcutorService.updateQueueMeta(consumerQueueDto);
//
//		consumerQueueDto = buildDefaultConsumerQueueDto();
//		MessageDto messageDto = new MessageDto();
//		messageDto.setId(consumerQueueDto.getOffset() + 1);
//		mqQueueExcutorService.updateLastId(consumerQueueDto, messageDto);
//		Field f = FieldUtils.getDeclaredField(MqQueueExcutorService.class, "consumerQueueRef", true);
//		@SuppressWarnings("unchecked")
//		AtomicReference<ConsumerQueueDto> consumerQueueRef = (AtomicReference<ConsumerQueueDto>) (f
//				.get(mqQueueExcutorService));
//		assertEquals("testUpdateLastIdMore error", consumerQueueRef.get().getLastId() + 1,
//				consumerQueueDto.getLastId());
//	}
//
//	@Test
//	public void testDoCommitOffsetVersionChange() throws NoSuchMethodException, SecurityException,
//			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
//		MockMqClientBase mockMqClientBase = new MockMqClientBase();
//		ConsumerQueueDto consumerQueueDto = buildDefaultConsumerQueueDto();
//		consumerQueueDto.setPullBatchSize(100);
//		MqQueueExcutorService mqQueueExcutorService = new MqQueueExcutorService(mockMqClientBase, consumerGroupName,
//				consumerQueueDto);
//
//		mqQueueExcutorService.updateQueueMeta(consumerQueueDto);
//		consumerQueueDto = buildDefaultConsumerQueueDto();
//		consumerQueueDto.setOffsetVersion(consumerQueueDto.getOffsetVersion() - 1);
//		mqQueueExcutorService.doCommit(consumerQueueDto);
//		assertEquals("testDoCommitOffsetVersionChange error", 0,
//				((MqQueueResource) mockMqClientBase.getContext().getMqResource()).getCommitFlag());
//	}
//
//	@Test
//	public void testDoCommitOffsetVersionNotChange() throws NoSuchMethodException, SecurityException,
//			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
//		MockMqClientBase mockMqClientBase = new MockMqClientBase();
//		ConsumerQueueDto consumerQueueDto = buildDefaultConsumerQueueDto();
//		consumerQueueDto.setPullBatchSize(100);
//		MqQueueExcutorService mqQueueExcutorService = new MqQueueExcutorService(mockMqClientBase, consumerGroupName,
//				consumerQueueDto);
//
//		mqQueueExcutorService.updateQueueMeta(consumerQueueDto);
//		consumerQueueDto = buildDefaultConsumerQueueDto();
//		mqQueueExcutorService.doCommit(consumerQueueDto);
//		assertEquals("testDoCommitOffsetVersionChange error", 0,
//				((MqQueueResource) mockMqClientBase.getContext().getMqResource()).getCommitFlag());
//	}
//
//	@Test
//	public void testHandleDataMsg0() {
//		MockMqClientBase mockMqClientBase = new MockMqClientBase();
//		ConsumerQueueDto consumerQueueDto = buildDefaultConsumerQueueDto();
//		consumerQueueDto.setPullBatchSize(100);
//		MqQueueExcutorService mqQueueExcutorService = new MqQueueExcutorService(mockMqClientBase, consumerGroupName,
//				consumerQueueDto);
//		mqQueueExcutorService.updateQueueMeta(consumerQueueDto);
//		mqQueueExcutorService.handleData();
//		assertEquals("testHandleDataMsg0 error", 0,
//				((MqQueueResource) mockMqClientBase.getContext().getMqResource()).getCommitFlag());
//	}
//
//	@Test
//	public void testHandleDataCheckPreHandFalse() {
//		MockMqClientBase mockMqClientBase = new MockMqClientBase();
//		mockMqClientBase.getContext().getMqEvent().setPreHandleListener(new PreHandleListener() {
//			@Override
//			public boolean preHandle(ConsumerQueueDto consumerQueue) {
//				// TODO Auto-generated method stub
//				return false;
//			}
//		});
//		ConsumerQueueDto consumerQueueDto = buildDefaultConsumerQueueDto();
//		consumerQueueDto.setPullBatchSize(100);
//		MqQueueExcutorService mqQueueExcutorService = new MqQueueExcutorService(mockMqClientBase, consumerGroupName,
//				consumerQueueDto);
//		mqQueueExcutorService.updateQueueMeta(consumerQueueDto);
//		mqQueueExcutorService.handleData();
//		assertEquals("testHandleDataMsg0 error", 0,
//				((MqQueueResource) mockMqClientBase.getContext().getMqResource()).getCommitFlag());
//	}
//
////	@Test
////	public void testHandleDataMsg20() {
////		MockMqClientBase mockMqClientBase = new MockMqClientBase();
////		ConsumerQueueDto consumerQueueDto = buildDefaultConsumerQueueDto();
////		consumerQueueDto.setPullBatchSize(20);
////		MqQueueExcutorService mqQueueExcutorService = new MqQueueExcutorService(mockMqClientBase, consumerGroupName,
////				consumerQueueDto);
////		mqQueueExcutorService.updateQueueMeta(consumerQueueDto);
////		mqQueueExcutorService.doPullingData();
////		int count = (int) ((consumerQueueDto.getPullBatchSize() + consumerQueueDto.getThreadSize() - 1)
////				/ consumerQueueDto.getThreadSize());
////		for (int i = 0; i < count; i++) {
////			mqQueueExcutorService.handleData();
////		}
////		// Util.sleep(5000);
////		assertEquals("testHandleDataMsg0 error", count,
////				((MqQueueResource) mockMqClientBase.getContext().getMqResource()).getCommitFlag());
////	}
//
//	@Test
//	public void testSendFailMailfailBeginTime0() {
//		MockMqClientBase mockMqClientBase = new MockMqClientBase();
//		ConsumerQueueDto consumerQueueDto = buildDefaultConsumerQueueDto();
//		consumerQueueDto.setPullBatchSize(20);
//		MqQueueExcutorService mqQueueExcutorService = new MqQueueExcutorService(mockMqClientBase, consumerGroupName,
//				consumerQueueDto);
//		mqQueueExcutorService.sendFailMail();
//
//		assertEquals("testSendFailMailfailBeginTime0 error", 0,
//				((MqQueueResource) mockMqClientBase.getContext().getMqResource()).getSendMailFlag());
//	}
//
//	@Test
//	public void testSendFailMailfailBeginTime50()
//			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
//		MockMqClientBase mockMqClientBase = new MockMqClientBase();
//		ConsumerQueueDto consumerQueueDto = buildDefaultConsumerQueueDto();
//		consumerQueueDto.setPullBatchSize(20);
//		MqQueueExcutorService mqQueueExcutorService = new MqQueueExcutorService(mockMqClientBase, consumerGroupName,
//				consumerQueueDto);
//		Field field = mqQueueExcutorService.getClass().getDeclaredField("failBeginTime");
//		field.setAccessible(true);
//		field.set(mqQueueExcutorService, System.currentTimeMillis() - 1);
//		mqQueueExcutorService.sendFailMail();
//
//		assertEquals("testSendFailMailfailBeginTime50 error", 0,
//				((MqQueueResource) mockMqClientBase.getContext().getMqResource()).getSendMailFlag());
//	}
//
//	@Test
//	public void testSendFailMailfailBeginTime70()
//			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
//		MockMqClientBase mockMqClientBase = new MockMqClientBase();
//		ConsumerQueueDto consumerQueueDto = buildDefaultConsumerQueueDto();
//		consumerQueueDto.setPullBatchSize(20);
//		MqQueueExcutorService mqQueueExcutorService = new MqQueueExcutorService(mockMqClientBase, consumerGroupName,
//				consumerQueueDto);
//		Field field = mqQueueExcutorService.getClass().getDeclaredField("failBeginTime");
//		field.setAccessible(true);
//		field.set(mqQueueExcutorService, System.currentTimeMillis() - 60 * 1000);
//		mqQueueExcutorService.sendFailMail();
//
//		assertEquals("testSendFailMailfailBeginTime50 error", 1,
//				((MqQueueResource) mockMqClientBase.getContext().getMqResource()).getSendMailFlag());
//	}
//
//	@Test
//	public void testCheckTagEmpty() {
//		MockMqClientBase mockMqClientBase = new MockMqClientBase();
//		ConsumerQueueDto consumerQueueDto = buildDefaultConsumerQueueDto();
//		consumerQueueDto.setTag("");
//		consumerQueueDto.setPullBatchSize(20);
//		MqQueueExcutorService mqQueueExcutorService = new MqQueueExcutorService(mockMqClientBase, consumerGroupName,
//				consumerQueueDto);
//		assertEquals("testCheckTagEmpty error", true,
//				mqQueueExcutorService.checkTag(consumerQueueDto, new MessageDto()));
//
//	}
//
//	@Test
//	public void testCheckTagContain() {
//		MockMqClientBase mockMqClientBase = new MockMqClientBase();
//		ConsumerQueueDto consumerQueueDto = buildDefaultConsumerQueueDto();
//		consumerQueueDto.setTag("123,213");
//		consumerQueueDto.setPullBatchSize(20);
//		MqQueueExcutorService mqQueueExcutorService = new MqQueueExcutorService(mockMqClientBase, consumerGroupName,
//				consumerQueueDto);
//		MessageDto messageDto = new MessageDto();
//		messageDto.setTag("123");
//		assertEquals("testCheckTagContain error", true, mqQueueExcutorService.checkTag(consumerQueueDto, messageDto));
//
//	}
//
//	@Test
//	public void testCheckTagNotContain() {
//		MockMqClientBase mockMqClientBase = new MockMqClientBase();
//		ConsumerQueueDto consumerQueueDto = buildDefaultConsumerQueueDto();
//		consumerQueueDto.setTag("123,213");
//		consumerQueueDto.setPullBatchSize(20);
//		MqQueueExcutorService mqQueueExcutorService = new MqQueueExcutorService(mockMqClientBase, consumerGroupName,
//				consumerQueueDto);
//		MessageDto messageDto = new MessageDto();
//		messageDto.setTag("1233");
//		assertEquals("testCheckTagContain error", false, mqQueueExcutorService.checkTag(consumerQueueDto, messageDto));
//	}
//
//	@Test
//	public void testGetFailMsgNormalTopic() {
//		MockMqClientBase mockMqClientBase = new MockMqClientBase();
//		ConsumerQueueDto consumerQueueDto = buildDefaultConsumerQueueDto();
//		MqQueueExcutorService mqQueueExcutorService = new MqQueueExcutorService(mockMqClientBase, consumerGroupName,
//				consumerQueueDto);
//		consumerQueueDto.setTopicType(1);
//		Map<Long, MessageDto> messageMap = new HashMap<Long, MessageDto>();
//		messageMap.put(1L, new MessageDto());
//		messageMap.put(2L, new MessageDto());
//		messageMap.put(3L, new MessageDto());
//		List<Long> failIds = new ArrayList<Long>();
//		failIds.add(1L);
//		List<Long> sucIds = new ArrayList<Long>();
//		Map<Long, MessageDto> rsMap = mqQueueExcutorService.getFailMsg(consumerQueueDto, failIds, sucIds, messageMap);
//		assertEquals("testGetFailMsgNormalTopic error", 1, rsMap.size());
//		assertEquals("testGetFailMsgNormalTopic error", 0, sucIds.size());
//	}
//
//	@Test
//	public void testGetFailMsgFailTopic() {
//		MockMqClientBase mockMqClientBase = new MockMqClientBase();
//		ConsumerQueueDto consumerQueueDto = buildDefaultConsumerQueueDto();
//		MqQueueExcutorService mqQueueExcutorService = new MqQueueExcutorService(mockMqClientBase, consumerGroupName,
//				consumerQueueDto);
//		consumerQueueDto.setTopicType(2);
//		Map<Long, MessageDto> messageMap = new HashMap<Long, MessageDto>();
//		messageMap.put(1L, new MessageDto());
//		messageMap.put(2L, new MessageDto());
//		messageMap.put(3L, new MessageDto());
//		List<Long> failIds = new ArrayList<Long>();
//		failIds.add(1L);
//		List<Long> sucIds = new ArrayList<Long>();
//		Map<Long, MessageDto> rsMap = mqQueueExcutorService.getFailMsg(consumerQueueDto, failIds, sucIds, messageMap);
//		assertEquals("testGetFailMsgNormalTopic error", 1, rsMap.size());
//		assertEquals("testGetFailMsgNormalTopic error", 2, sucIds.size());
//	}
//
//	@Test
//	public void testCheckRetryCountNormalTopic() {
//		MockMqClientBase mockMqClientBase = new MockMqClientBase();
//		ConsumerQueueDto consumerQueueDto = buildDefaultConsumerQueueDto();
//		MqQueueExcutorService mqQueueExcutorService = new MqQueueExcutorService(mockMqClientBase, consumerGroupName,
//				consumerQueueDto);
//		consumerQueueDto.setTopicType(1);
//		assertEquals("testCheckRetryCountNormalTopic error", true,
//				mqQueueExcutorService.checkRetryCount(new MessageDto(), consumerQueueDto));
//	}
//
//	@Test
//	public void testCheckRetryCountFailTopicMore() {
//		MockMqClientBase mockMqClientBase = new MockMqClientBase();
//		ConsumerQueueDto consumerQueueDto = buildDefaultConsumerQueueDto();
//		MqQueueExcutorService mqQueueExcutorService = new MqQueueExcutorService(mockMqClientBase, consumerGroupName,
//				consumerQueueDto);
//		consumerQueueDto.setTopicType(2);
//		consumerQueueDto.setRetryCount(3);
//		MessageDto messageDto = new MessageDto();
//		messageDto.setRetryCount(1);
//		assertEquals("testCheckRetryCountFailTopicMore error", true,
//				mqQueueExcutorService.checkRetryCount(messageDto, consumerQueueDto));
//	}
//
//	@Test
//	public void testCheckRetryCountFailTopicLess() {
//		MockMqClientBase mockMqClientBase = new MockMqClientBase();
//		ConsumerQueueDto consumerQueueDto = buildDefaultConsumerQueueDto();
//		MqQueueExcutorService mqQueueExcutorService = new MqQueueExcutorService(mockMqClientBase, consumerGroupName,
//				consumerQueueDto);
//		consumerQueueDto.setTopicType(2);
//		consumerQueueDto.setRetryCount(3);
//		MessageDto messageDto = new MessageDto();
//		messageDto.setRetryCount(4);
//		assertEquals("testCheckRetryCountFailTopicMore error", false,
//				mqQueueExcutorService.checkRetryCount(messageDto, consumerQueueDto));
//	}
//
//	@Test
//	public void testDoMessageReceivedTimeout0() {
//		MockMqClientBase mockMqClientBase = new MockMqClientBase();
//		ConsumerQueueDto consumerQueueDto = buildDefaultConsumerQueueDto();
//		MqQueueExcutorService mqQueueExcutorService = new MqQueueExcutorService(mockMqClientBase, consumerGroupName,
//				consumerQueueDto);
//		try {
//			List<Long> rs = mqQueueExcutorService.doMessageReceived(Arrays.asList(new MessageDto()));
//			assertEquals("testDoMessageReceivedTimeout0 error", true, rs == null || rs.size() == 0);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
//
//	@Test
//	public void testDoMessageReceivedTimeout0Fail() {
//		MockMqClientBase mockMqClientBase = new MockMqClientBase();
//		mockMqClientBase.getMqQueueSub().setFail(true);
//		ConsumerQueueDto consumerQueueDto = buildDefaultConsumerQueueDto();
//		MqQueueExcutorService mqQueueExcutorService = new MqQueueExcutorService(mockMqClientBase, consumerGroupName,
//				consumerQueueDto);
//		try {
//			List<Long> rs = mqQueueExcutorService.doMessageReceived(Arrays.asList(new MessageDto()));
//			assertEquals("testDoMessageReceivedTimeout0 error", 1, rs.size());
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
//
//	@Test
//	public void testDoMessageReceivedTimeout0Error() {
//		MockMqClientBase mockMqClientBase = new MockMqClientBase();
//		mockMqClientBase.getMqQueueSub().setErrorFlag(true);
//		ConsumerQueueDto consumerQueueDto = buildDefaultConsumerQueueDto();
//		MqQueueExcutorService mqQueueExcutorService = new MqQueueExcutorService(mockMqClientBase, consumerGroupName,
//				consumerQueueDto);
//		boolean flag = true;
//		try {
//			List<Long> rs = mqQueueExcutorService.doMessageReceived(Arrays.asList(new MessageDto()));
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			flag = false;
//		}
//		assertEquals("testDoMessageReceivedTimeout0Error error", false, flag);
//	}
//
//	@Test
//	public void testDoMessageReceivedTimeout1Timeout() {
//		MockMqClientBase mockMqClientBase = new MockMqClientBase();
//		mockMqClientBase.getMqQueueSub().setSleepTime(5);
//		ConsumerQueueDto consumerQueueDto = buildDefaultConsumerQueueDto();
//		consumerQueueDto.setTimeout(3);
//		MqQueueExcutorService mqQueueExcutorService = new MqQueueExcutorService(mockMqClientBase, consumerGroupName,
//				consumerQueueDto);
//		boolean flag = true;
//		try {
//			List<Long> rs = mqQueueExcutorService.doMessageReceived(Arrays.asList(new MessageDto()));
//			// assertEquals("testDoMessageReceivedTimeout1 error", true, rs == null ||
//			// rs.size() == 0);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			flag = false;
//		}
//		assertEquals("testDoMessageReceivedTimeout1 error", false, flag);
//	}
//
//	@Test
//	public void testDoMessageReceivedTimeout1() {
//		MockMqClientBase mockMqClientBase = new MockMqClientBase();
//		mockMqClientBase.getMqQueueSub().setSleepTime(5);
//		ConsumerQueueDto consumerQueueDto = buildDefaultConsumerQueueDto();
//		consumerQueueDto.setConsumerGroupName(consumerGroupName);
//		consumerQueueDto.setTopicName(topicName);
//		consumerQueueDto.setTimeout(6);
//		Util.sleep(10000);
//		MqQueueExcutorService mqQueueExcutorService = new MqQueueExcutorService(mockMqClientBase, consumerGroupName,
//				consumerQueueDto);
//		boolean flag = true;
//		try {
//			List<Long> rs = mqQueueExcutorService.doMessageReceived(Arrays.asList(new MessageDto()));
//			assertEquals("testDoMessageReceivedTimeout1 error", true, rs == null || rs.size() == 0);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			flag = false;
//		}
//		assertEquals("testDoMessageReceivedTimeout1 error", true, flag);
//	}
//
//	@Test
//	public void testClearTrace() {
//		MockMqClientBase mockMqClientBase = new MockMqClientBase();
//		mockMqClientBase.getMqQueueSub().setSleepTime(1);
//		ConsumerQueueDto consumerQueueDto = buildDefaultConsumerQueueDto();
//		consumerQueueDto.setTimeout(2);
//		MqQueueExcutorService mqQueueExcutorService = new MqQueueExcutorService(mockMqClientBase, consumerGroupName,
//				consumerQueueDto);
//		mqQueueExcutorService.clearTrace();
//	}
//
//	@Test
//	public void testCheckDelay0() {
//		MockMqClientBase mockMqClientBase = new MockMqClientBase();
//		mockMqClientBase.getMqQueueSub().setSleepTime(1);
//		ConsumerQueueDto consumerQueueDto = buildDefaultConsumerQueueDto();
//		consumerQueueDto.setTimeout(2);
//		MqQueueExcutorService mqQueueExcutorService = new MqQueueExcutorService(mockMqClientBase, consumerGroupName,
//				consumerQueueDto);
//		consumerQueueDto.setDelayProcessTime(0);
//		long start = System.currentTimeMillis();
//		boolean rs = mqQueueExcutorService.checkDelay(new MessageDto(), consumerQueueDto);
//		assertEquals("testCheckDelay0 error ", true, System.currentTimeMillis() - start < 100);
//		assertEquals("testCheckDelay0 error ", true, rs);
//	}
//
//	@Test
//	public void testCheckDelay2() {
//		MockMqClientBase mockMqClientBase = new MockMqClientBase();
//		mockMqClientBase.getMqQueueSub().setSleepTime(1);
//		ConsumerQueueDto consumerQueueDto = buildDefaultConsumerQueueDto();
//		MqQueueExcutorService mqQueueExcutorService = new MqQueueExcutorService(mockMqClientBase, consumerGroupName,
//				consumerQueueDto);
//		consumerQueueDto.setDelayProcessTime(3);
//		MessageDto messageDto = new MessageDto();
//		messageDto.setSendTime(new Date());
//		long start = System.currentTimeMillis();
//		mqQueueExcutorService.checkDelay(messageDto, consumerQueueDto);
//		assertEquals("testCheckDelay2 error ", true, System.currentTimeMillis() - start > 1000);
//	}
//
//	@Test
//	public void testStart() {
//		MockMqClientBase mockMqClientBase = new MockMqClientBase();
//		mockMqClientBase.getMqQueueSub().setSleepTime(1);
//		ConsumerQueueDto consumerQueueDto = buildDefaultConsumerQueueDto();
//		MqQueueExcutorService mqQueueExcutorService = new MqQueueExcutorService(mockMqClientBase, consumerGroupName,
//				consumerQueueDto);
//		mqQueueExcutorService.updateQueueMeta(consumerQueueDto);
//		mqQueueExcutorService.start();
//	}
//
//	@Test
//	public void testClose() {
//		MockMqClientBase mockMqClientBase = new MockMqClientBase();
//		mockMqClientBase.getMqQueueSub().setSleepTime(1);
//		ConsumerQueueDto consumerQueueDto = buildDefaultConsumerQueueDto();
//		MqQueueExcutorService mqQueueExcutorService = new MqQueueExcutorService(mockMqClientBase, consumerGroupName,
//				consumerQueueDto);
//		mqQueueExcutorService.updateQueueMeta(consumerQueueDto);
//		mqQueueExcutorService.start();
//		mqQueueExcutorService.close();
//	}
//
//	private static class MqQueueResource extends AbstractMockResource {
//		private volatile int commitFlag = 0;
//		private volatile int addLogFlag = 0;
//		private volatile int sendMailFlag = 0;
//		private volatile int publishAndUpdateResultFailMsgFlag = 0;
//
//		public int getPublishAndUpdateResultFailMsgFlag() {
//			return publishAndUpdateResultFailMsgFlag;
//		}
//
//		public void setPublishAndUpdateResultFailMsgFlag(int publishAndUpdateResultFailMsgFlag) {
//			this.publishAndUpdateResultFailMsgFlag = publishAndUpdateResultFailMsgFlag;
//		}
//
//		public int getSendMailFlag() {
//			return sendMailFlag;
//		}
//
//		public void setSendMailFlag(int sendMailFlag) {
//			this.sendMailFlag = sendMailFlag;
//		}
//
//		public int getAddLogFlag() {
//			return addLogFlag;
//		}
//
//		public void setAddLogFlag(int addLogFlag) {
//			this.addLogFlag = addLogFlag;
//		}
//
//		@Override
//		public void commitOffset(CommitOffsetRequest request) {
//			// TODO Auto-generated method stub
//			commitFlag++;
//		}
//
//		public int getCommitFlag() {
//			return commitFlag;
//		}
//
//		public void setCommitFlag(int commitFlag) {
//			this.commitFlag = commitFlag;
//		}
//
//		public void addLog(LogRequest request) {
//			addLogFlag++;
//		}
//
//		public void sendMail(SendMailRequest request) {
//			sendMailFlag++;
//		}
//
//		public void publishAndUpdateResultFailMsg(FailMsgPublishAndUpdateResultRequest request) {
//			publishAndUpdateResultFailMsgFlag++;
//		}
//
//		@Override
//		public PullDataResponse pullData(PullDataRequest request) {
//			List<MessageDto> rs = new ArrayList<MessageDto>(
//					(int) (request.getOffsetEnd() - request.getOffsetStart() + 1));
//			for (long i = request.getOffsetStart() + 1; i <= request.getOffsetEnd(); i++) {
//				MessageDto messageDto = new MessageDto();
//				messageDto.setId(i);
//				messageDto.setConsumerGroupName(consumerGroupName);
//				messageDto.setRetryCount(0);
//				messageDto.setTopicName(topicName);
//				messageDto.setTraceId("" + i);
//				messageDto.setBody(i + "");
//				messageDto.setSendTime(new Date());
//				rs.add(messageDto);
//			}
//			PullDataResponse pullDataResponse = new PullDataResponse();
//			pullDataResponse.setMsgs(rs);
//			pullDataResponse.setSuc(true);
//			return pullDataResponse;
//		}
//
//		@Override
//		public String getBrokerIp() {
//			// TODO Auto-generated method stub
//			return null;
//		}
//	}
//
//	public static class MqQueueSub implements ISubscriber {
//
//		public volatile int count = 0;
//		public volatile boolean failFlag = false;
//		public volatile boolean errorFlag = false;
//		public volatile int sleepTime = 0;
//
//		public int getSleepTime() {
//			return sleepTime;
//		}
//
//		public void setSleepTime(int sleepTime) {
//			this.sleepTime = sleepTime;
//		}
//
//		public boolean isErrorFlag() {
//			return errorFlag;
//		}
//
//		public void setErrorFlag(boolean errorFlag) {
//			this.errorFlag = errorFlag;
//		}
//
//		public int getCount() {
//			return count;
//		}
//
//		public void setCount(int count) {
//			this.count = count;
//		}
//
//		@Override
//		public synchronized List<Long> onMessageReceived(List<MessageDto> messages) {			
//			count += messages.size();
//			if (sleepTime > 0) {
//				Util.sleep(sleepTime * 1000);
//			}
//			if (!failFlag && !errorFlag) {
//				return null;
//			} else if (errorFlag) {
//				throw new RuntimeException();
//			} else {
//				List<Long> ids = new ArrayList<Long>(messages.size());
//				messages.forEach(t1 -> ids.add(t1.getId()));
//				return ids;
//			}
//		}
//
//		public void setFail(boolean failFlag) {
//			this.failFlag = failFlag;
//		}
//
//	}
//
//	public static class MqSubSelector implements ISubscriberSelector {
//
//		MqQueueSub mqQueueSub = new MqQueueSub();
//
//		@Override
//		public ISubscriber getSubscriber(String consumerGroupName, String topic) {
//			// TODO Auto-generated method stub
//			return mqQueueSub;
//		}
//
//		public MqQueueSub getMqQueueSub() {
//			return mqQueueSub;
//		}
//
//	}
//
//	private static class MockMqClientBase extends AbstractMockMqClientBase {
//		private MqContext mqContext = new MqContext();
//		MqSubSelector mqSubSelector = new MqSubSelector();
//
//		public MockMqClientBase() {
//			mqContext.setConsumerId(1);
//			mqContext.setConsumerName("test");
//			Map<String, Long> consumerGroupVersionMap = new HashMap<String, Long>();
//			consumerGroupVersionMap.put(consumerGroupName, 0L);
//			ConsumerGroupOneDto consumerGroupOneDto = buildConsumerGroupOne();
//			mqContext.getConsumerGroupMap().put(consumerGroupName, consumerGroupOneDto);
//			mqContext.setConsumerGroupVersion(consumerGroupVersionMap);
//			mqContext.getConfig().setRbTimes(0);
//			mqContext.setMqResource(new MqQueueResource());
//			mqContext.getMqEvent().setiSubscriberSelector(mqSubSelector);
//		}
//
//		@Override
//		public MqContext getContext() {
//			// TODO Auto-generated method stub
//			return mqContext;
//		}
//
//		public MqQueueSub getMqQueueSub() {
//			return mqSubSelector.getMqQueueSub();
//		}
//
//		@Override
//		public boolean publish(String topic, String token, ProducerDataDto message,
//				IPartitionSelector iPartitionSelector) throws MqNotInitException, ContentExceed65535Exception {
//			// TODO Auto-generated method stub
//			return false;
//		}
//
//		@Override
//		public boolean publish(String topic, String token, List<ProducerDataDto> messages,
//				IPartitionSelector iPartitionSelector) throws MqNotInitException, ContentExceed65535Exception {
//			// TODO Auto-generated method stub
//			return false;
//		}
//	};
//}
