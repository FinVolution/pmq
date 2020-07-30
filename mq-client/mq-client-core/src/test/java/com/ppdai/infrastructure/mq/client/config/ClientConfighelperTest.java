package com.ppdai.infrastructure.mq.client.config;

import static org.junit.Assert.assertEquals;

import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.ppdai.infrastructure.mq.client.MqContext;
import com.ppdai.infrastructure.mq.client.config.ClientConfigHelper;
import com.ppdai.infrastructure.mq.client.config.ConsumerGroupVo;

@RunWith(JUnit4.class)
public class ClientConfighelperTest {

	@Test
	public void getConfigTest() {
		ClientConfigHelper clientConfigHelper = new ClientConfigHelper(new MqContext());
		Map<String, ConsumerGroupVo> maps = clientConfigHelper.getConfig();
		assertEquals("messageQueue.xml 解析数量不对！", 1, maps.size());
		assertEquals("topic 解析数量不对！", 1, maps.get("testSub").getTopics().size());
	}

	String consumerError = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\r\n" + "<messageQueue>\r\n"
			+ "     <consumer1 groupName=\"testSub\">\r\n" + "        <topics>\r\n"
			+ "            <topic name=\"test\"  receiverType=\"com.ppdai.infrastructure.mq.client.TestSub1\"></topic>\r\n"
			+ "        </topics>\r\n" + "    </consumer1>\r\n" + "</messageQueue>";

	String consumerGroupError = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\r\n" + "<messageQueue>\r\n"
			+ "     <consumer groupName1=\"testSub\">\r\n" 
			+ "            <topic name=\"test\"  receiverType=\"com.ppdai.infrastructure.mq.client.TestSub1\"></topic>\r\n"
			+ "    </consumer>\r\n" + "</messageQueue>";

	String consumerGroupTopicEmptyError = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\r\n" + "<messageQueue>\r\n"
			+ "     <consumer groupName=\"testSub\">\r\n" 
			+ "    </consumer>\r\n" + "</messageQueue>";

	String consumerTopicNameError = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\r\n" + "<messageQueue>\r\n"
			+ "     <consumer groupName=\"testSub\">\r\n" 
			+ "            <topic name1=\"test\"  receiverType=\"com.ppdai.infrastructure.mq.client.TestSub\"></topic>\r\n"
			+ "     </consumer>\r\n" + "</messageQueue>";

	String noReceiverTypeError = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\r\n" + "<messageQueue>\r\n"
			+ "     <consumer groupName=\"testSub\">\r\n" 
			+ "            <topic name=\"test\"  ></topic>\r\n"
			+ "     </consumer>\r\n" + "</messageQueue>";
	
	String receiverTypeError = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\r\n" + "<messageQueue>\r\n"
			+ "     <consumer groupName=\"testSub\">\r\n" 
			+ "            <topic name=\"test\"  receiverType=\"com.ppdai.infrastructure.mq.client.TestErrorSub\"></topic>\r\n"
			+ "     </consumer>\r\n" + "</messageQueue>";
	
	String receiverNotExistTypeError = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\r\n" + "<messageQueue>\r\n"
			+ "     <consumer groupName=\"testSub\">\r\n"
			+ "            <topic name=\"test\"  receiverType=\"com.ppdai.infrastructure.mq.client.TestErrorSub1\"></topic>\r\n"
			+ "     </consumer>\r\n" + "</messageQueue>";

	String twoConsumerGroups = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\r\n" + "<messageQueue>\r\n"
			+ "     <consumer groupName=\"testSub\">\r\n"
			+ "            <topic name=\"test\"  receiverType=\"com.ppdai.infrastructure.mq.client.TestSub\"></topic>\r\n"
			+ "     </consumer>\r\n" + "    <consumer groupName=\"testSub1\">\r\n"		
			+ "            <topic name=\"test\"  receiverType=\"com.ppdai.infrastructure.mq.client.TestSub\"></topic>\r\n"
			+ "     </consumer>\r\n" + "</messageQueue>";

	String twoConsumerGroups1 = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\r\n" + "<messageQueue>\r\n"
			+ "	<consumers>\r\n" + "		<consumer groupName=\"testSub\">\r\n" 
			+ "				<topic name=\"test\"\r\n"
			+ "					receiverType=\"com.ppdai.infrastructure.mq.client.TestSub\"></topic>\r\n"
			+ "			</consumer>\r\n" + "		<consumer groupName=\"testSub1\">\r\n"
			+ "			<topic name=\"test\"\r\n"
			+ "					receiverType=\"com.ppdai.infrastructure.mq.client.TestSub\"></topic>\r\n"
			+ "			</consumer>\r\n" + "	</consumers>\r\n" + "</messageQueue>";

	@Test
	public void getConfigXmlTest() {
		try {
			Map<String, ConsumerGroupVo> maps = ClientConfigHelper.getConfig(consumerError);
			assertEquals("consumerError 解析异常", 1, maps.size());
		} catch (Exception e) {
			// TODO: handle exception
		}
		try {
			Map<String, ConsumerGroupVo> maps = ClientConfigHelper.getConfig(consumerGroupError);
			assertEquals("consumerGroupError 解析异常", 1, maps.size());
		} catch (Exception e) {
			// TODO: handle exception
		}
		try {
			Map<String, ConsumerGroupVo> maps = ClientConfigHelper.getConfig(consumerGroupTopicEmptyError);
			assertEquals("consumerGroupTopicEmptyError 解析异常", 1, maps.size());
		} catch (Exception e) {
			// TODO: handle exception
		}
		try {
			Map<String, ConsumerGroupVo> maps = ClientConfigHelper.getConfig(consumerTopicNameError);
			assertEquals("consumerTopicNameError 解析异常", 1, maps.size());
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		try {
			Map<String, ConsumerGroupVo> maps = ClientConfigHelper.getConfig(receiverTypeError);
			assertEquals("receiverTypeError 解析异常", 1, maps.size());
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		try {
			Map<String, ConsumerGroupVo> maps = ClientConfigHelper.getConfig(receiverNotExistTypeError);
			assertEquals("receiverNotExistTypeError 解析异常", 1, maps.size());
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		try {
			Map<String, ConsumerGroupVo> maps = ClientConfigHelper.getConfig(noReceiverTypeError);
			assertEquals("noReceiverTypeError 解析异常", 1, maps.size());
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		
		
		Map<String, ConsumerGroupVo> maps = ClientConfigHelper.getConfig(twoConsumerGroups);
		assertEquals("twoConsumerGroups 解析异常", 2, maps.size());

		maps = ClientConfigHelper.getConfig(twoConsumerGroups1);
		assertEquals("twoConsumerGroups1 解析异常", 2, maps.size());

		assertEquals(null, ClientConfigHelper.getConfig(null));
		
		boolean rs=false;
		try {
			ClientConfigHelper.getConfig("tete");
		} catch (Exception e) {
			rs=true;
		}
		assertEquals(true, rs);
	}
}
