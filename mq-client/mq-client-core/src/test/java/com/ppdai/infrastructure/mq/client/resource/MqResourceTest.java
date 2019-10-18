package com.ppdai.infrastructure.mq.client.resource;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.ppdai.infrastructure.mq.biz.common.util.BrokerException;
import com.ppdai.infrastructure.mq.biz.common.util.IHttpClient;
import com.ppdai.infrastructure.mq.biz.dto.MqConstanst;
import com.ppdai.infrastructure.mq.biz.dto.base.MessageDto;
import com.ppdai.infrastructure.mq.biz.dto.client.CatRequest;
import com.ppdai.infrastructure.mq.biz.dto.client.CommitOffsetRequest;
import com.ppdai.infrastructure.mq.biz.dto.client.CommitOffsetResponse;
import com.ppdai.infrastructure.mq.biz.dto.client.ConsumerDeRegisterRequest;
import com.ppdai.infrastructure.mq.biz.dto.client.ConsumerDeRegisterResponse;
import com.ppdai.infrastructure.mq.biz.dto.client.ConsumerGroupOneDto;
import com.ppdai.infrastructure.mq.biz.dto.client.ConsumerGroupRegisterRequest;
import com.ppdai.infrastructure.mq.biz.dto.client.ConsumerGroupRegisterResponse;
import com.ppdai.infrastructure.mq.biz.dto.client.ConsumerRegisterRequest;
import com.ppdai.infrastructure.mq.biz.dto.client.ConsumerRegisterResponse;
import com.ppdai.infrastructure.mq.biz.dto.client.FailMsgPublishAndUpdateResultRequest;
import com.ppdai.infrastructure.mq.biz.dto.client.FailMsgPublishAndUpdateResultResponse;
import com.ppdai.infrastructure.mq.biz.dto.client.GetConsumerGroupRequest;
import com.ppdai.infrastructure.mq.biz.dto.client.GetConsumerGroupResponse;
import com.ppdai.infrastructure.mq.biz.dto.client.GetGroupTopicRequest;
import com.ppdai.infrastructure.mq.biz.dto.client.GetGroupTopicResponse;
import com.ppdai.infrastructure.mq.biz.dto.client.GetMessageCountRequest;
import com.ppdai.infrastructure.mq.biz.dto.client.GetMessageCountResponse;
import com.ppdai.infrastructure.mq.biz.dto.client.GetMetaGroupRequest;
import com.ppdai.infrastructure.mq.biz.dto.client.GetMetaGroupResponse;
import com.ppdai.infrastructure.mq.biz.dto.client.GetTopicQueueIdsRequest;
import com.ppdai.infrastructure.mq.biz.dto.client.GetTopicQueueIdsResponse;
import com.ppdai.infrastructure.mq.biz.dto.client.GetTopicRequest;
import com.ppdai.infrastructure.mq.biz.dto.client.GetTopicResponse;
import com.ppdai.infrastructure.mq.biz.dto.client.GroupTopicDto;
import com.ppdai.infrastructure.mq.biz.dto.client.HeartbeatRequest;
import com.ppdai.infrastructure.mq.biz.dto.client.HeartbeatResponse;
import com.ppdai.infrastructure.mq.biz.dto.client.LogRequest;
import com.ppdai.infrastructure.mq.biz.dto.client.OpLogRequest;
import com.ppdai.infrastructure.mq.biz.dto.client.PublishMessageRequest;
import com.ppdai.infrastructure.mq.biz.dto.client.PublishMessageResponse;
import com.ppdai.infrastructure.mq.biz.dto.client.PullDataRequest;
import com.ppdai.infrastructure.mq.biz.dto.client.PullDataResponse;
import com.ppdai.infrastructure.mq.biz.dto.client.SendMailRequest;
import com.ppdai.infrastructure.mq.biz.dto.client.SendMailResponse;
import com.ppdai.infrastructure.mq.client.AbstractTest;

@RunWith(JUnit4.class)
public class MqResourceTest extends AbstractTest {
	@Test
	public void constructorTest() {
		boolean rs = false;
		try {
			new MqResource(null, 10000, 10000);
		} catch (Exception e) {
			rs = true;
		}
		assertEquals("constructorTest error", true, rs);
		new MqResource("http://localhost", 10000, 10000);
	}

	@Test
	public void setUrlsNullTest() {
		MqResource mqResource = new MqResource("http://localhost", 10000, 10000);
		mqResource.setUrls(null, null);
		AtomicReference<List<String>> urlsG1 = getValue("urlsG1", mqResource);
		AtomicReference<List<String>> urlsG2 = getValue("urlsG2", mqResource);

		Map<String, Long> failUrlG1 = getValue("failUrlG1", mqResource);
		Map<String, Long> failUrlG2 = getValue("failUrlG1", mqResource);

		assertEquals("setUrlsNullTest null", 0, urlsG1.get().size());
		assertEquals("setUrlsNullTest null", 0, urlsG2.get().size());
		assertEquals("setUrlsNullTest null", 0, failUrlG1.size());
		assertEquals("setUrlsNullTest null", 0, failUrlG2.size());

	}

	@Test
	public void setUrlsNotNullTest() {
		MqResource mqResource = new MqResource("http://localhost", 10000, 10000);
		mqResource.setUrls(Arrays.asList("1", "1", "1"), Arrays.asList("1", "1", "1"));
		AtomicReference<List<String>> urlsG1 = getValue("urlsG1", mqResource);
		AtomicReference<List<String>> urlsG2 = getValue("urlsG2", mqResource);

		Map<String, Long> failUrlG1 = getValue("failUrlG1", mqResource);
		Map<String, Long> failUrlG2 = getValue("failUrlG1", mqResource);

		assertEquals("setUrlsNullTest null", 3, urlsG1.get().size());
		assertEquals("setUrlsNullTest null", 3, urlsG2.get().size());
		assertEquals("setUrlsNullTest null", 0, failUrlG1.size());
		assertEquals("setUrlsNullTest null", 0, failUrlG2.size());
	}

	@Test
	public void getHostTrueAndFail0Test() {
		MqResource mqResource = new MqResource("http://localhost", 10000, 10000);
		String rs = mqResource.getHost(true);
		assertEquals("getHostG1G2NullAndFail0Test null", "http://localhost", rs);
		mqResource.setUrls(Arrays.asList("1", "2"), null);

		rs = mqResource.getHost(true);
		assertEquals("getHostG1G2NullAndFail0Test null", true, rs.equals("1") || rs.equals("2"));

		String rs1 = mqResource.getHost(true);
		if (rs.equals("1")) {
			assertEquals("getHostTrueAndFail0Test null", "2", rs1);
		} else {
			assertEquals("getHostTrueAndFail0Test null", "1", rs1);
		}
	}

	@Test
	public void getHostTrueAndFail1Test() {
		MqResource mqResource = new MqResource("http://localhost", 10000, 10000);
		String rs = mqResource.getHost(true);
		assertEquals("getHostG1G2NullAndFail0Test null", "http://localhost", rs);
		mqResource.setUrls(Arrays.asList("1", "2"), null);
		Map<String, Long> failUrlG1 = new HashMap<String, Long>();
		failUrlG1.put("1", System.currentTimeMillis() - 9 * 1000);
		failUrlG1.put("2", System.currentTimeMillis() - 9 * 1000);
		setValue("failUrlG1", failUrlG1, mqResource);
		rs = mqResource.getHost(true);
		assertEquals("getHostG1G2NullAndFail0Test null", "http://localhost", rs);

		rs = mqResource.getHost(true);
		assertEquals("getHostG1G2NullAndFail0Test null", "http://localhost", rs);

		failUrlG1.put("1", System.currentTimeMillis() - 11 * 1000);
		failUrlG1.put("2", System.currentTimeMillis() - 9 * 1000);
		rs = mqResource.getHost(true);
		assertEquals("getHostG1G2NullAndFail0Test null", "1", rs);
	}

	@Test
	public void getHostFalseAndFail0Test() {
		MqResource mqResource = new MqResource("http://localhost", 10000, 10000);
		String rs = mqResource.getHost(false);
		assertEquals("getHostFalseAndFail0Test null", "http://localhost", rs);
		mqResource.setUrls(null, Arrays.asList("1", "2"));

		rs = mqResource.getHost(false);
		assertEquals("getHostFalseAndFail0Test null", true, rs.equals("1") || rs.equals("2"));

		String rs1 = mqResource.getHost(false);
		if (rs.equals("1")) {
			assertEquals("getHostFalseAndFail0Test null", "2", rs1);
		} else {
			assertEquals("getHostFalseAndFail0Test null", "1", rs1);
		}
	}

	@Test
	public void getHostFalseAndFail1Test() {
		MqResource mqResource = new MqResource("http://localhost", 10000, 10000);
		String rs = mqResource.getHost(true);
		assertEquals("getHostFalseAndFail1Test error", "http://localhost", rs);
		mqResource.setUrls(null, Arrays.asList("1", "2"));
		Map<String, Long> failUrlG2 = new HashMap<String, Long>();
		failUrlG2.put("1", System.currentTimeMillis() - 7 * 1000);
		failUrlG2.put("2", System.currentTimeMillis() - 7 * 1000);
		setValue("failUrlG2", failUrlG2, mqResource);
		rs = mqResource.getHost(false);
		assertEquals("getHostFalseAndFail1Test error", "http://localhost", rs);

		rs = mqResource.getHost(false);
		assertEquals("getHostFalseAndFail1Test error", "http://localhost", rs);

		failUrlG2.put("1", System.currentTimeMillis() - 11 * 1000);
		failUrlG2.put("2", System.currentTimeMillis() - 9 * 1000);
		rs = mqResource.getHost(false);
		assertEquals("getHostFalseAndFail1Test error", "1", rs);
	}

	@Test
	public void registerNullTest() {
		MqResource mqResource = new MqResource("http://localhost", 10000, 10000);
		assertEquals("registerNullTest error", 0, mqResource.register(null));
	}

	@Test
	public void registerSucTest() throws IOException, BrokerException {
		IHttpClient httpClient = mock(IHttpClient.class);
		MqResource mqResource = new MqResource(httpClient, "http://localhost");
		ConsumerRegisterResponse registerResponse = new ConsumerRegisterResponse();
		registerResponse.setId(10);
		registerResponse.setMsg("test");
		when(httpClient.post(anyString(), anyObject(), eq(ConsumerRegisterResponse.class)))
				.thenReturn(registerResponse);
		assertEquals("registerSucTest error", 10, mqResource.register(new ConsumerRegisterRequest()));
	}

	@Test
	public void registerFailTest() throws IOException, BrokerException {
		IHttpClient httpClient = mock(IHttpClient.class);
		MqResource mqResource = new MqResource(httpClient, "http://localhost");
		when(httpClient.post(anyString(), anyObject(), eq(ConsumerRegisterResponse.class)))
				.thenThrow(new BrokerException("error"));
		boolean rs = false;
		try {
			mqResource.register(new ConsumerRegisterRequest());
		} catch (Exception e) {
			rs = true;
		}
		assertEquals("registerFailTest error", true, rs);
	}

	@Test
	public void publishAndUpdateResultFailMsgNullTest() throws IOException, BrokerException {
		IHttpClient httpClient = mock(IHttpClient.class);
		MqResource mqResource = new MqResource(httpClient, "http://localhost");

		mqResource.publishAndUpdateResultFailMsg(null);
	}

	@Test
	public void publishAndUpdateResultFailMsgSucTest() throws IOException, BrokerException {
		IHttpClient httpClient = mock(IHttpClient.class);
		MqResource mqResource = new MqResource(httpClient, "http://localhost");
		when(httpClient.post(anyString(), anyObject(), eq(FailMsgPublishAndUpdateResultResponse.class)))
				.thenThrow(new BrokerException("error"));
		mqResource.publishAndUpdateResultFailMsg(new FailMsgPublishAndUpdateResultRequest());
	}

	@Test
	public void deRegisterNullTest() throws IOException, BrokerException {
		IHttpClient httpClient = mock(IHttpClient.class);
		MqResource mqResource = new MqResource(httpClient, "http://localhost");

		mqResource.deRegister(null);
	}

	@Test
	public void deRegisterSucTest() throws IOException, BrokerException {
		IHttpClient httpClient = mock(IHttpClient.class);
		MqResource mqResource = new MqResource(httpClient, "http://localhost");
		when(httpClient.post(anyString(), anyObject(), eq(ConsumerDeRegisterResponse.class)))
				.thenReturn(new ConsumerDeRegisterResponse());
		mqResource.deRegister(new ConsumerDeRegisterRequest());
	}

	@Test
	public void getMetaGroupNullTest() throws IOException, BrokerException {
		IHttpClient httpClient = mock(IHttpClient.class);
		MqResource mqResource = new MqResource(httpClient, "http://localhost");

		assertEquals("getMetaGroupNullTest error", null, mqResource.getMetaGroup(null));
	}

	@Test
	public void getMetaGroupSucTest() throws IOException, BrokerException {
		IHttpClient httpClient = mock(IHttpClient.class);
		MqResource mqResource = new MqResource(httpClient, "http://localhost");
		GetMetaGroupResponse response = new GetMetaGroupResponse();
		response.setBrokerIpG1(Arrays.asList("1", "2", "3"));
		response.setBrokerIpG2(Arrays.asList("1", "2", "3"));
		when(httpClient.post(anyString(), anyObject(), eq(GetMetaGroupResponse.class))).thenReturn(response);
		GetMetaGroupResponse rGetMetaGroupResponse = mqResource.getMetaGroup(new GetMetaGroupRequest());
		assertEquals("getMetaGroupSucTest error", 3, rGetMetaGroupResponse.getBrokerIpG1().size());
	}

	@Test
	public void getTopicNullTest() throws IOException, BrokerException {
		IHttpClient httpClient = mock(IHttpClient.class);
		MqResource mqResource = new MqResource(httpClient, "http://localhost");
		assertEquals("getTopicNullTest error", null, mqResource.getTopic(null));
	}

	@Test
	public void getTopicSucTest() throws IOException, BrokerException {
		IHttpClient httpClient = mock(IHttpClient.class);
		MqResource mqResource = new MqResource(httpClient, "http://localhost");
		GetTopicResponse response = new GetTopicResponse();
		response.setTopics(Arrays.asList("1", "2", "3"));
		when(httpClient.post(anyString(), anyObject(), eq(GetTopicResponse.class))).thenReturn(response);
		GetTopicResponse rrespnse = mqResource.getTopic(new GetTopicRequest());
		assertEquals("getTopicSucTest error", 3, rrespnse.getTopics().size());
	}

	@Test
	public void getGroupTopicNullTest() throws IOException, BrokerException {
		IHttpClient httpClient = mock(IHttpClient.class);
		MqResource mqResource = new MqResource(httpClient, "http://localhost");
		assertEquals("getGroupTopicNullTest error", null, mqResource.getGroupTopic(null));
	}

	@Test
	public void getGroupTopicSucTest() throws IOException, BrokerException {
		IHttpClient httpClient = mock(IHttpClient.class);
		MqResource mqResource = new MqResource(httpClient, "http://localhost");
		GetGroupTopicResponse response = new GetGroupTopicResponse();
		response.setGroupTopics(Arrays.asList(new GroupTopicDto()));
		when(httpClient.post(anyString(), anyObject(), eq(GetGroupTopicResponse.class))).thenReturn(response);
		GetGroupTopicResponse rrespnse = mqResource.getGroupTopic(new GetGroupTopicRequest());
		assertEquals("getGroupTopicSucTest error", 1, rrespnse.getGroupTopics().size());
	}

	@Test
	public void addCatNullTest() throws IOException, BrokerException {
		IHttpClient httpClient = mock(IHttpClient.class);
		MqResource mqResource = new MqResource(httpClient, "http://localhost");
		mqResource.addCat(null);
	}

	@Test
	public void addCatSucTest() throws IOException, BrokerException {
		IHttpClient httpClient = mock(IHttpClient.class);
		MqResource mqResource = new MqResource(httpClient, "http://localhost");
		mqResource.addCat(new CatRequest());
	}

	@Test
	public void publishNullTest() {
		MqResource mqResource = new MqResource("http://localhost", 10000, 10000);
		assertEquals("publishNullTest error", true, mqResource.publish(null));
	}

	@Test
	public void publishFailTest() throws IOException, BrokerException {
		IHttpClient httpClient = mock(IHttpClient.class);
		MqResource mqResource = new MqResource(httpClient, "http://localhost");
		PublishMessageResponse response = new PublishMessageResponse();
		response.setSuc(false);
		response.setMsg("test");
		//response.setTime(2L);
		when(httpClient.post(anyString(), anyObject(), eq(PublishMessageResponse.class))).thenReturn(response);
		assertEquals("publishFailTest error", false, mqResource.publish(new PublishMessageRequest()));
	}

	@Test
	public void publishSucTest() throws IOException, BrokerException {
		IHttpClient httpClient = mock(IHttpClient.class);
		MqResource mqResource = new MqResource(httpClient, "http://localhost");
		PublishMessageResponse response = new PublishMessageResponse();
		response.setSuc(true);
		response.setMsg("test");
		response.setTime(2L);
		when(httpClient.post(anyString(), anyObject(), eq(PublishMessageResponse.class))).thenReturn(response);
		assertEquals("publishSucTest error", true, mqResource.publish(new PublishMessageRequest()));
	}

	@Test
	public void commitOffsetNullTest() throws IOException, BrokerException {
		IHttpClient httpClient = mock(IHttpClient.class);
		MqResource mqResource = new MqResource(httpClient, "http://localhost");
		mqResource.commitOffset(null);
//		PublishMessageResponse response=new PublishMessageResponse();
//		response.setSuc(true);
//		response.setMsg("test");
//		response.setTime(2L);
//		when(httpClient.post(anyString(), anyObject(), eq(PublishMessageResponse.class))).thenReturn(response);		
//		assertEquals("publishNullTest error", true, mqResource.publish(new PublishMessageRequest()));
	}

	@Test
	public void commitOffsetSucTest() throws IOException, BrokerException {
		IHttpClient httpClient = mock(IHttpClient.class);
		MqResource mqResource = new MqResource(httpClient, "http://localhost");
		mqResource.commitOffset(null);
		CommitOffsetResponse response = new CommitOffsetResponse();
		response.setSuc(true);
		response.setMsg("test");
		response.setTime(2L);
		when(httpClient.post(anyString(), anyObject(), eq(CommitOffsetResponse.class))).thenReturn(response);
		mqResource.commitOffset(new CommitOffsetRequest());
	}

	@Test
	public void commitOffsetFailTest() throws IOException, BrokerException {
		IHttpClient httpClient = mock(IHttpClient.class);
		MqResource mqResource = new MqResource(httpClient, "http://localhost");
		mqResource.commitOffset(null);
		when(httpClient.post(anyString(), anyObject(), eq(CommitOffsetResponse.class)))
				.thenThrow(new BrokerException("Test"));
		mqResource.commitOffset(new CommitOffsetRequest());
	}

	@Test
	public void registerConsumerGroupNullTest() {
		IHttpClient httpClient = mock(IHttpClient.class);
		MqResource mqResource = new MqResource(httpClient, "http://localhost");
		assertEquals("registerConsumerGroupNullTest error", null, mqResource.registerConsumerGroup(null));
	}

	@Test
	public void registerConsumerGroupSucTest() throws IOException, BrokerException {
		IHttpClient httpClient = mock(IHttpClient.class);
		MqResource mqResource = new MqResource(httpClient, "http://localhost");
		ConsumerGroupRegisterResponse response = new ConsumerGroupRegisterResponse();
		response.setSuc(false);
		when(httpClient.post(anyString(), anyObject(), eq(ConsumerGroupRegisterResponse.class))).thenReturn(response);
		assertEquals("registerConsumerGroupSucTest error", false,
				mqResource.registerConsumerGroup(new ConsumerGroupRegisterRequest()).isSuc());
	}

	@Test
	public void registerConsumerGroupErrorTest() throws IOException, BrokerException {
		IHttpClient httpClient = mock(IHttpClient.class);
		MqResource mqResource = new MqResource(httpClient, "http://localhost");
		ConsumerGroupRegisterResponse response = new ConsumerGroupRegisterResponse();
		response.setSuc(false);
		when(httpClient.post(anyString(), anyObject(), eq(ConsumerGroupRegisterResponse.class)))
				.thenThrow(new BrokerException("error"));
		boolean rs = false;
		try {
			mqResource.registerConsumerGroup(new ConsumerGroupRegisterRequest()).isSuc();
		} catch (Exception e) {
			rs = true;
		}
		assertEquals("registerConsumerGroupErrorTest error", true, rs);
	}

	@Test
	public void heartbeatNullTest() {
		IHttpClient httpClient = mock(IHttpClient.class);
		MqResource mqResource = new MqResource(httpClient, "http://localhost");
		mqResource.heartbeat(null);
	}

	@Test
	public void heartbeatSucTest() throws IOException, BrokerException {
		IHttpClient httpClient = mock(IHttpClient.class);
		MqResource mqResource = new MqResource(httpClient, "http://localhost");
		HeartbeatResponse response = new HeartbeatResponse();
		response.setSuc(false);
		when(httpClient.post(anyString(), anyObject(), eq(HeartbeatResponse.class))).thenReturn(response);
		mqResource.heartbeat(new HeartbeatRequest());
	}

	@Test
	public void getConsumerGroupNullTest() {
		IHttpClient httpClient = mock(IHttpClient.class);
		MqResource mqResource = new MqResource(httpClient, "http://localhost");
		assertEquals("getConsumerGroupNullTest error", null, mqResource.getConsumerGroup(null));
	}

	@Test
	public void getConsumerGroupSucTest() throws IOException, BrokerException {
		IHttpClient httpClient = mock(IHttpClient.class);
		MqResource mqResource = new MqResource(httpClient, "http://localhost");
		GetConsumerGroupResponse response = new GetConsumerGroupResponse();
		response.setSuc(false);
		response.setConsumerGroups(new HashMap<String, ConsumerGroupOneDto>());
		response.getConsumerGroups().put("test", new ConsumerGroupOneDto());
		when(httpClient.post(anyString(), anyObject(), eq(GetConsumerGroupResponse.class))).thenReturn(response);
		assertEquals("getConsumerGroupSucTest error", 1,
				mqResource.getConsumerGroup(new GetConsumerGroupRequest()).getConsumerGroups().size());
	}

	@Test
	public void getMessageCountNullTest() {
		IHttpClient httpClient = mock(IHttpClient.class);
		MqResource mqResource = new MqResource(httpClient, "http://localhost");
		assertEquals("getMessageCountNullTest error", null, mqResource.getMessageCount(null));
	}

	@Test
	public void getMessageCountSucTest() throws IOException, BrokerException {
		IHttpClient httpClient = mock(IHttpClient.class);
		MqResource mqResource = new MqResource(httpClient, "http://localhost");
		GetMessageCountResponse response = new GetMessageCountResponse();
		response.setSuc(false);
		response.setCount(1);
		when(httpClient.post(anyString(), anyObject(), eq(GetMessageCountResponse.class))).thenReturn(response);
		assertEquals("getMessageCountSucTest error", 1,
				mqResource.getMessageCount(new GetMessageCountRequest()).getCount());
	}

	@Test
	public void pullDataNullTest() {
		IHttpClient httpClient = mock(IHttpClient.class);
		MqResource mqResource = new MqResource(httpClient, "http://localhost");
		assertEquals("pullDataNullTest error", null, mqResource.pullData(null));
	}

	@Test
	public void pullDataSucTest() throws IOException, BrokerException {
		IHttpClient httpClient = mock(IHttpClient.class);
		MqResource mqResource = new MqResource(httpClient, "http://localhost");
		PullDataResponse response = new PullDataResponse();
		response.setSuc(false);
		response.setMsgs(Arrays.asList(new MessageDto()));
		when(httpClient.post(anyString(), anyObject(), eq(PullDataResponse.class))).thenReturn(response);
		assertEquals("pullDataSucTest error", 1, mqResource.pullData(new PullDataRequest()).getMsgs().size());
	}

	@Test
	public void pullDataErrorTest() throws IOException, BrokerException {
		IHttpClient httpClient = mock(IHttpClient.class);
		MqResource mqResource = new MqResource(httpClient, "http://localhost");

		when(httpClient.post(anyString(), anyObject(), eq(PullDataResponse.class)))
				.thenThrow(new BrokerException("error"));
		boolean rs = false;
		try {
			mqResource.pullData(new PullDataRequest());
		} catch (Exception e) {
			rs = true;
		}
		assertEquals("pullDataErrorTest error", true, rs);
	}

	@Test
	public void getTopicQueueIdsNullTest() {
		IHttpClient httpClient = mock(IHttpClient.class);
		MqResource mqResource = new MqResource(httpClient, "http://localhost");
		assertEquals("getTopicQueueIdsNullTest error", null, mqResource.getTopicQueueIds(null));
	}

	@Test
	public void getTopicQueueIdsSucTest() throws IOException, BrokerException {
		IHttpClient httpClient = mock(IHttpClient.class);
		MqResource mqResource = new MqResource(httpClient, "http://localhost");
		GetTopicQueueIdsResponse response = new GetTopicQueueIdsResponse();
		response.setSuc(false);
		response.setTopicQueues(new HashMap<String, List<Long>>());
		response.getTopicQueues().put("Test", new ArrayList<Long>());
		// response.setMsgs(Arrays.asList(new MessageDto()));
		when(httpClient.post(anyString(), anyObject(), eq(GetTopicQueueIdsResponse.class))).thenReturn(response);
		assertEquals("getTopicQueueIdsSucTest error", 1,
				mqResource.getTopicQueueIds(new GetTopicQueueIdsRequest()).getTopicQueues().size());
	}

	@Test
	public void getTopicQueueIdsErrorTest() throws IOException, BrokerException {
		IHttpClient httpClient = mock(IHttpClient.class);
		MqResource mqResource = new MqResource(httpClient, "http://localhost");

		when(httpClient.post(anyString(), anyObject(), eq(GetTopicQueueIdsResponse.class)))
				.thenThrow(new BrokerException("error"));
		boolean rs = false;
		try {
			mqResource.getTopicQueueIds(new GetTopicQueueIdsRequest());
		} catch (Exception e) {
			rs = true;
		}
		assertEquals("getTopicQueueIdsErrorTest error", true, rs);
	}

	@Test
	public void addLogTest() {
		IHttpClient httpClient = mock(IHttpClient.class);
		MqResource mqResource = new MqResource(httpClient, "http://localhost");
		mqResource.addLog(null);
		mqResource.addLog(new LogRequest());
	}

	@Test
	public void addOpLogTest() {
		IHttpClient httpClient = mock(IHttpClient.class);
		MqResource mqResource = new MqResource(httpClient, "http://localhost");
		mqResource.addOpLog(null);
		mqResource.addOpLog(new OpLogRequest());
	}

	@Test
	public void sendMailTest() {
		IHttpClient httpClient = mock(IHttpClient.class);
		MqResource mqResource = new MqResource(httpClient, "http://localhost");
		mqResource.sendMail(null);
		mqResource.sendMail(new SendMailRequest());
	}
	
	@Test
	public void postTest() throws IOException, BrokerException {
		IHttpClient httpClient = mock(IHttpClient.class);
		MqResource mqResource = new MqResource(httpClient, "http://localhost");		
		PublishMessageResponse response = new PublishMessageResponse();
		response.setSuc(false);
		response.setMsg("test");
		response.setSleepTime(2L);
		when(httpClient.post(anyString(), anyObject(), eq(PublishMessageResponse.class))).thenThrow(new IOException("ttt"));
		boolean rs=false;
		try {
			mqResource.post(new Object(), MqConstanst.CONSUMERPRE + "/publish", 2, PublishMessageResponse.class, true);
		} catch (Exception e) {
			rs=true;
		}
		assertEquals("publish error", true,rs);
		
		when(httpClient.post(anyString(), anyObject(), eq(PublishMessageResponse.class))).thenThrow(new BrokerException("ttt"));
		rs=false;
		try {
			mqResource.post(new Object(), MqConstanst.CONSUMERPRE + "/publish", 2, PublishMessageResponse.class, true);
		} catch (Exception e) {
			rs=true;
		}
		assertEquals("publish error", true,rs);		
		when(httpClient.post(anyString(), anyObject(), eq(PublishMessageResponse.class))).thenReturn(response);
		new Thread(new Runnable() {			
			@Override
			public void run() {
				long start=System.currentTimeMillis();
				while (true) {
					if(System.currentTimeMillis()-start>1000) {
						response.setSleepTime(0);
					}
				}				
			}
		}).start();
		assertEquals("publish error", response, mqResource.post(new Object(), MqConstanst.CONSUMERPRE + "/publish", 2, PublishMessageResponse.class, true));
		SendMailResponse response2=new SendMailResponse();
		response2.setCode(MqConstanst.NO);
		response2.setSuc(false);
		when(httpClient.post(anyString(), anyObject(), eq(SendMailResponse.class))).thenReturn(response2);
		assertEquals("sendMail error", null, mqResource.post(new Object(), MqConstanst.CONSUMERPRE + "/sendMail", 2, SendMailResponse.class, true));
		
	}
}
