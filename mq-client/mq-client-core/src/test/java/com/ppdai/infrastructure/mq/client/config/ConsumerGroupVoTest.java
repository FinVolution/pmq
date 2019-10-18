package com.ppdai.infrastructure.mq.client.config;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.ppdai.infrastructure.mq.biz.dto.base.MessageDto;
import com.ppdai.infrastructure.mq.biz.event.ISubscriber;

@RunWith(JUnit4.class)
public class ConsumerGroupVoTest {

	@Test
	public void ConsumerGroupNoArrVoTest() {
		ConsumerGroupVo consumerGroupVo = new ConsumerGroupVo();
		consumerGroupVo = new ConsumerGroupVo("fasdfasf");
	}

	@Test
	public void ConsumerGroupVoNullTest() {
		boolean rs = false;
		try {
			ConsumerGroupVo consumerGroupVo = new ConsumerGroupVo(null, new ConsumerGroupTopicVo());
		} catch (Exception e) {
			rs = true;
		}
		assertEquals("ConsumerGroupVoNullTest error", true, rs);

		rs = false;
		try {
			ConsumerGroupVo consumerGroupVo = new ConsumerGroupVo("tttt", null);
		} catch (Exception e) {
			rs = true;
		}
		assertEquals("ConsumerGroupVoNullTest error", true, rs);

		rs = false;
		try {
			ConsumerGroupVo consumerGroupVo = new ConsumerGroupVo("ttt", new ConsumerGroupTopicVo());
		} catch (Exception e) {
			rs = true;
		}
		assertEquals("ConsumerGroupVoNullTest error", true, rs);

		rs = false;
		try {
			ConsumerGroupVo consumerGroupVo = new ConsumerGroupVo("ttt", new ConsumerGroupTopicVo("ttt", null));
		} catch (Exception e) {
			rs = true;
		}
		assertEquals("ConsumerGroupVoNullTest error", false, rs);
	}

	@Test
	public void setGroupNameTest() {
		ConsumerGroupVo consumerGroupVo = new ConsumerGroupVo("tttt");
		boolean rs = false;
		try {
			consumerGroupVo = new ConsumerGroupVo();
			consumerGroupVo.setGroupName(null);
		} catch (Exception e) {
			rs = true;
		}
		assertEquals("setGroupNameTest error", true, rs);
		consumerGroupVo = new ConsumerGroupVo("tttt");
		consumerGroupVo.setGroupName("tttt2");
		assertEquals("setGroupNameTest error", "tttt", consumerGroupVo.getMeta().getName());

		consumerGroupVo = new ConsumerGroupVo();
		consumerGroupVo.setGroupName("tttt2");
		assertEquals("setGroupNameTest error", "tttt2", consumerGroupVo.getMeta().getName());

	}

	@Test
	public void addTopicTest() {
		ConsumerGroupVo consumerGroupVo = new ConsumerGroupVo("tttt");
		boolean rs = false;
		try {
			consumerGroupVo.addTopic(null);
		} catch (Exception e) {
			rs = true;
		}
		assertEquals("addTopicTest error", true, rs);

		rs = false;
		try {
			consumerGroupVo.addTopic(new ConsumerGroupTopicVo(null, null));
		} catch (Exception e) {
			rs = true;
		}
		assertEquals("addTopicTest error", true, rs);
		rs = false;
		rs = consumerGroupVo.addTopic(new ConsumerGroupTopicVo("test", null));
		assertEquals("addTopicTest error", true, rs);
		rs = consumerGroupVo.addTopic(new ConsumerGroupTopicVo("test", null));
		assertEquals("addTopicTest error", false, rs);
	}
	
	@Test
	public void otherTest() {
		ConsumerGroupVo consumerGroupVo = new ConsumerGroupVo("tttt");
		consumerGroupVo.setTopics(null);
		consumerGroupVo.getTopics();
		
		ConsumerGroupTopicVo consumerGroupTopicVo=new ConsumerGroupTopicVo("tt", new ISubscriber() {
			
			@Override
			public List<Long> onMessageReceived(List<MessageDto> messages) {
				// TODO Auto-generated method stub
				return null;
			}
		});
		
		consumerGroupTopicVo.getSubscriber();
		
		ConsumerGroupMetaVo consumerGroupMetaVo=new ConsumerGroupMetaVo();
		consumerGroupMetaVo.setName("t");
		consumerGroupMetaVo.setOriginName("t");
		consumerGroupMetaVo.getName();
		consumerGroupMetaVo.getOriginName();
	}
}
