package com.ppdai.infrastructure.mq.biz.common.config;

import org.apache.catalina.connector.Connector;
import org.apache.coyote.ProtocolHandler;
import org.apache.coyote.http11.Http11NioProtocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.embedded.MimeMappings;
import org.springframework.boot.context.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.ppdai.infrastructure.mq.biz.common.SoaConfig;

@Component
public class TomcatContainerCustomizer implements EmbeddedServletContainerCustomizer {
	private static final Logger logger = LoggerFactory.getLogger(TomcatContainerCustomizer.class);
	private static final String TOMCAT_ACCEPTOR_COUNT = "server.tomcat.accept-count";
	@Autowired
	private Environment environment;
	@Autowired
	private SoaConfig soaConfig;
	private volatile int acceptCount = 0;
	private volatile int maxThreads = 100;
	private volatile int minThreads = 10;

	@Override
	public void customize(ConfigurableEmbeddedServletContainer container) {
		MimeMappings mappings = new MimeMappings(MimeMappings.DEFAULT);
		mappings.add("woff", "application/x-font-woff");
		mappings.add("eot", "application/vnd.ms-fontobject");
		mappings.add("ttf", "application/x-font-ttf");
		container.setMimeMappings(mappings);

		if (!(container instanceof TomcatEmbeddedServletContainerFactory)) {
			return;
		}
		if (!environment.containsProperty(TOMCAT_ACCEPTOR_COUNT)) {
			return;
		}
		TomcatEmbeddedServletContainerFactory tomcat = (TomcatEmbeddedServletContainerFactory) container;
		tomcat.addConnectorCustomizers(new TomcatConnectorCustomizer() {

			@Override
			public void customize(Connector connector) {
				ProtocolHandler handler = connector.getProtocolHandler();
				if (handler instanceof Http11NioProtocol) {
					Http11NioProtocol http = (Http11NioProtocol) handler;
					acceptCount = soaConfig.getTomcatAcceptCount();
					maxThreads = soaConfig.getTomcatMaxThreads();
					minThreads = soaConfig.getTomcatMinThreads();
					soaConfig.registerChanged(() -> {						
						if (maxThreads != soaConfig.getTomcatMaxThreads()) {
							maxThreads = soaConfig.getTomcatMaxThreads();
							if (maxThreads > 0) {
								http.setMaxThreads(maxThreads);
							} else {
								http.setMaxThreads(200);
							}
						}
						if (minThreads != soaConfig.getTomcatMinThreads()) {
							minThreads = soaConfig.getTomcatMinThreads();
							if (minThreads > 0) {
								http.setMinSpareThreads(minThreads);
							} else {
								http.setMinSpareThreads(10);
							}
						}


					});
					http.setBacklog(acceptCount);
					http.setMaxThreads(maxThreads);
					http.setMinSpareThreads(minThreads);
					logger.info("Setting tomcat accept count to {}", acceptCount);
				}
			}
		});
	}
}
