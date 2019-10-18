package com.ppdai.infrastructure.mq.biz.common.trace.spi;


public interface MessageProducerManager {
  /**
   * @return the message producer
   */
  MessageProducer getProducer();
}
