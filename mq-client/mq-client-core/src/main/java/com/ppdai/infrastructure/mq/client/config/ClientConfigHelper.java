package com.ppdai.infrastructure.mq.client.config;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.ppdai.infrastructure.mq.biz.common.util.Util;
import com.ppdai.infrastructure.mq.biz.event.ISubscriber;
import com.ppdai.infrastructure.mq.client.MqContext;

public class ClientConfigHelper {
	private static Logger log = LoggerFactory.getLogger(ClientConfigHelper.class);
	private MqContext mqContext;

	public ClientConfigHelper(MqContext mqContext) {
		this.mqContext = mqContext;
	}

	public ClientConfigHelper() {

	}

	public Map<String, ConsumerGroupVo> getConfig() {
		try {
			InputStream inputStream = getConfigFileStream();
			return getConfig(inputStream);
		} catch (Exception ex) {
			log.error("加载配置文件异常，异常信息：" + ex.getMessage(), ex);
			throw new RuntimeException(ex);
		}

	}

	public static Map<String, ConsumerGroupVo> getConfig(String xml) {
		if (Util.isEmpty(xml)) {
			return null;
		}
		ClientConfigHelper configHelper = new ClientConfigHelper();
		InputStream inputStream;
		try {
			inputStream = new ByteArrayInputStream(xml.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			log.error("xml 解析异常", e);
			return null;
		}
		return configHelper.getConfig(inputStream);
	}

	private Map<String, ConsumerGroupVo> getConfig(InputStream inputStream) {
		if (inputStream == null) {
			return null;
		}
		Document document = loadDocument(inputStream);
		Element rootElement = document.getDocumentElement();
		Map<String, ConsumerGroupVo> config = getConsumerConfig(rootElement);
		return config;
	}

	private Map<String, ConsumerGroupVo> getConsumerConfig(final Element element) {
		if (element == null || !element.hasChildNodes()) {
			throw new IllegalArgumentException("配置文件没有子节点异常");
		}
		NodeList nodeList = element.getElementsByTagName("consumer");
		if (nodeList == null || nodeList.getLength() < 1) {
			nodeList = element.getElementsByTagName("consumers");
			if (nodeList == null || nodeList.getLength() < 1) {
				throw new IllegalArgumentException("messageQueue.xml or mq.xml 文件中没有找到consumer节点");
			}
			nodeList = ((Element) nodeList.item(0)).getElementsByTagName("consumer");
		}
		Map<String, ConsumerGroupVo> data = new ConcurrentHashMap<>();
		int count = nodeList.getLength();
		for (int i = 0; i < count; i++) {
			Element item = (Element) nodeList.item(i);
			ConsumerGroupVo consumerGroupVo = getConfig(item);
			data.put(consumerGroupVo.getMeta().getName(), consumerGroupVo);
		}
		return data;
	}

	private ConsumerGroupVo getConfig(Element consumerItem) {
		ConsumerGroupVo consumerGroupConfig = new ConsumerGroupVo();
		if (consumerItem.hasAttribute("groupName")) {
			consumerGroupConfig.setGroupName(consumerItem.getAttribute("groupName"));
		} else {
			throw new IllegalArgumentException("consumer节点下的groupName参数不能为空");
		}
		setTopic(consumerGroupConfig, consumerItem);
		return consumerGroupConfig;
	}

	private void setTopic(ConsumerGroupVo consumerGroupConfig, Element consumerItem) {
		Map<String, ConsumerGroupTopicVo> groupConfigMap = new ConcurrentHashMap<>();
		if (consumerItem == null || !consumerItem.hasChildNodes()) {
			throw new IllegalArgumentException(consumerGroupConfig.getMeta().getName() + "下无topic节点");
		}
		NodeList nodeList = consumerItem.getElementsByTagName("topic");
		if (nodeList == null || nodeList.getLength() < 1) {
			throw new IllegalArgumentException(consumerGroupConfig.getMeta().getName() + "下无topic节点");
		}
		int count = nodeList.getLength();
		for (int i = 0; i < count; i++) {
			Element item = (Element) nodeList.item(i);
			if (item.getNodeType() != Node.ELEMENT_NODE) {
				continue;
			}
			if (!item.hasAttribute("name")) {
				throw new IllegalArgumentException("topic节点没有设置name参数");
			}
			ConsumerGroupTopicVo groupConfig = new ConsumerGroupTopicVo();
			groupConfig.setName(item.getAttribute("name"));
			if (item.hasAttribute("receiverType")) {
				groupConfig.setSubscriber(getSubscriber(item.getAttribute("receiverType")));

			} else {
				throw new IllegalArgumentException("topic:" + groupConfig.getName() + "节点没有设置receiverType参数");
			}
			groupConfigMap.put(groupConfig.getName(), groupConfig);
		}
		consumerGroupConfig.setTopics(groupConfigMap);

	}

	private ISubscriber getSubscriber(String receiverType) {
		ISubscriber subscriber = null;
		try {
			Class<?> onwClass = Class.forName(receiverType);
			if (ISubscriber.class.isAssignableFrom(onwClass)) {
				subscriber = (ISubscriber) onwClass.newInstance();
			}
		} catch (Exception ex) {
			throw new RuntimeException(receiverType + "不存在!", ex);
		}
		if (subscriber == null) {
			throw new RuntimeException(receiverType + "不存在!");
		}
		return subscriber;
	}
	
	private InputStream getConfigFileStream() {
		InputStream inputStream = ClientConfigHelper.class.getClassLoader().getResourceAsStream("messageQueue/mq.xml");
		if (inputStream != null) {
			mqContext.setConfigPath(
					ClientConfigHelper.class.getClassLoader().getResource("messageQueue/mq.xml").getPath());
		} else {
			inputStream = ClientConfigHelper.class.getClassLoader()
					.getResourceAsStream("messageQueue/messageQueue.xml");
			if (inputStream != null) {
				mqContext.setConfigPath(ClientConfigHelper.class.getClassLoader()
						.getResource("messageQueue/messageQueue.xml").getPath());
			}

		}
		return inputStream;
	}

	private Document loadDocument(InputStream inputStream) {
		Document document = null;
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder builder = builderFactory.newDocumentBuilder();
			document = builder.parse(inputStream);
		} catch (Exception e) {
			log.error(String.format("配置文件加载异常，异常信息:%s", e.getMessage()), e);
			throw new RuntimeException(e);
		} finally {
			try {
				inputStream.close();
			} catch (Exception ex) {
				// ex.printStackTrace();
			}
		}
		return document;
	}
}
