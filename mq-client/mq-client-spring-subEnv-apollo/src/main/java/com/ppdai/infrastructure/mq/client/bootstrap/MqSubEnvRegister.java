package com.ppdai.infrastructure.mq.client.bootstrap;

import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;
import org.springframework.stereotype.Component;

import com.ppdai.infrastructure.mq.biz.MqConst;
import com.ppdai.infrastructure.mq.biz.MqEnv;
import com.ppdai.infrastructure.mq.biz.common.util.Util;
import com.ppdai.infrastructure.mq.biz.dto.base.MessageDto;
import com.ppdai.infrastructure.mq.biz.dto.base.ProducerDataDto;
import com.ppdai.infrastructure.mq.biz.event.IMsgFilter;
import com.ppdai.infrastructure.mq.biz.event.PreSendListener;
import com.ppdai.infrastructure.mq.client.MqClient;
import com.ppdai.infrastructure.mq.client.MqEnvironment;

@Component
public class MqSubEnvRegister implements BeanFactoryPostProcessor, PriorityOrdered {
	private static AtomicBoolean initFlag = new AtomicBoolean(false);

	private void initMqEnvironment() {
		MqClient.setMqEnvironment(new MqEnvironment() {
			@Override
			public void setTargetSubEnv(String targetSubEnv1) {
				ApolloEnvironment.setTargetSubEnv(targetSubEnv1);
			}

			@Override
			public boolean isPro() {

				return ApolloEnvironment.isPro();
			}

			@Override
			public String getTargetSubEnv() {
				// TODO Auto-generated method stub
				return ApolloEnvironment.getTargetSubEnv();
			}

			@Override
			public String getSubEnv() {
				// TODO Auto-generated method stub
				return ApolloEnvironment.getSubEnv();
			}

			@Override
			public MqEnv getEnv() {
				// TODO Auto-generated method stub
				return MqEnv.fromString(ApolloEnvironment.getEnv().toString());
			}

			@Override
			public Set<String> getAppSubEnvs() {
				return MqClient.getContext().getAppSubEnv();
			}

			@Override
			public String getAppId() {
				// TODO Auto-generated method stub
				return ApolloEnvironment.getAppId();
			}

			@Override
			public void clear() {
				ApolloEnvironment.clear();
			}

			@Override
			public void setAppSubEnvs(List<String> appSubEnvs) {
				ApolloEnvironment.setAppSubEnvs(appSubEnvs);
			}
		});

	}

	@Override
	public int getOrder() {
		// TODO Auto-generated method stub
		return Ordered.HIGHEST_PRECEDENCE + 1;
	}

	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		if (initFlag.compareAndSet(false, true)) {
			initMqEnvironment();
			registerEvent();
		}
	}

	private void registerEvent() {
		MqClient.registerPreSendEvent(new PreSendListener() {
			@Override
			public void onPreSend(ProducerDataDto message) {
				if (MqClient.getMqEnvironment() != null) {
					if (MqEnv.FAT == MqClient.getMqEnvironment().getEnv()) {
						if (message.getHead() == null) {
							message.setHead(new HashMap<>());
						}
						String subEnv = MqClient.getMqEnvironment().getSubEnv();
						if (!Util.isEmpty(MqClient.getMqEnvironment().getTargetSubEnv())) {
							subEnv = MqClient.getMqEnvironment().getTargetSubEnv();
						}
						if (!MqConst.DEFAULT_SUBENV.equalsIgnoreCase(subEnv)) {
							message.getHead().put(MqConst.MQ_SUB_ENV_KEY, subEnv);
						}
					}
				}
			}
		});
		MqClient.registerMsgFilterEvent(new IMsgFilter() {
			@Override
			public boolean onMsgFilter(MessageDto messageDto) {
				if (MqClient.getMqEnvironment() != null) {
					if (MqEnv.FAT != MqClient.getMqEnvironment().getEnv()) {
						return true;
					}
					if (messageDto.getHead() == null) {
						messageDto.setHead(new HashMap<>());
					}
					String subEnv = messageDto.getHead().get(MqConst.MQ_SUB_ENV_KEY);
					if (Util.isEmpty(subEnv)) {
						subEnv = MqConst.DEFAULT_SUBENV;
					}
					if (!MqConst.DEFAULT_SUBENV.equals(MqClient.getMqEnvironment().getSubEnv())) {
						return MqClient.getMqEnvironment().getSubEnv().equalsIgnoreCase(subEnv);
					} else if (MqConst.DEFAULT_SUBENV.equals(subEnv)) {
						return true;
					} else if (!MqConst.DEFAULT_SUBENV.equals(subEnv)
							&& MqClient.getMqEnvironment().getAppSubEnvs() != null
							&& MqClient.getMqEnvironment().getAppSubEnvs().contains(subEnv)) {
						return false;
					}
				}
				return true;
			}
		});
	}

}
