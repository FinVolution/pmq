package com.ppdai.infrastructure.mq.client;

import java.util.List;
import java.util.Map;

import com.ppdai.infrastructure.mq.biz.dto.base.ProducerDataDto;
import com.ppdai.infrastructure.mq.biz.dto.client.GroupTopicDto;
import com.ppdai.infrastructure.mq.biz.event.IPartitionSelector;
import com.ppdai.infrastructure.mq.client.MqClient.IMqClientBase;
import com.ppdai.infrastructure.mq.client.config.ConsumerGroupVo;
import com.ppdai.infrastructure.mq.client.exception.ContentExceed65535Exception;
import com.ppdai.infrastructure.mq.client.exception.MqNotInitException;
import com.ppdai.infrastructure.mq.client.factory.IMqFactory;

public class AbstractMockMqClientBase implements IMqClientBase {

	@Override
	public boolean isAsynAvailable() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean start() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean start(String brokerUrl) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean start(MqConfig config) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void reStart() {
		// TODO Auto-generated method stub

	}

	@Override
	public void init(MqConfig config) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean hasInit() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean registerConsumerGroup(Map<String, ConsumerGroupVo> groups) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean registerConsumerGroup(ConsumerGroupVo consumerGroup) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void checkBody(ProducerDataDto message) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean publish(String topic, String token, ProducerDataDto message)
			throws MqNotInitException, ContentExceed65535Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean publish(String topic, String token, List<ProducerDataDto> messages)
			throws MqNotInitException, ContentExceed65535Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void publishAsyn(String topic, String token, List<ProducerDataDto> messages,
			IPartitionSelector iPartitionSelector) throws MqNotInitException, ContentExceed65535Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void publishAsyn(String topic, String token, ProducerDataDto message)
			throws MqNotInitException, ContentExceed65535Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void publishAsyn(String topic, String token, ProducerDataDto message, IPartitionSelector iPartitionSelector)
			throws MqNotInitException, ContentExceed65535Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public MqContext getContext() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long fetchMessageCount(String groupName, List<String> topicNames) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub

	}

	@Override
	public void close() {
		// TODO Auto-generated method stub

	}

	@Override
	public List<String> getTopic(String consumerGroupName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<GroupTopicDto> getGroupTopic(List<String> consumerGroupNames) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IMqFactory getMqFactory() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean publish(String topic, String token, ProducerDataDto message, IPartitionSelector iPartitionSelector)
			throws MqNotInitException, ContentExceed65535Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean publish(String topic, String token, List<ProducerDataDto> messages,
			IPartitionSelector iPartitionSelector) throws MqNotInitException, ContentExceed65535Exception {
		// TODO Auto-generated method stub
		return false;
	}

}
