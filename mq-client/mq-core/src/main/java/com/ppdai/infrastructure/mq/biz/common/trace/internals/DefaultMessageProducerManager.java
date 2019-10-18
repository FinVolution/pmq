package com.ppdai.infrastructure.mq.biz.common.trace.internals;



import com.ppdai.infrastructure.mq.biz.common.trace.internals.cat.CatMessageProducer;
import com.ppdai.infrastructure.mq.biz.common.trace.internals.cat.CatNames;
import com.ppdai.infrastructure.mq.biz.common.trace.spi.MessageProducer;
import com.ppdai.infrastructure.mq.biz.common.trace.spi.MessageProducerManager;
import com.ppdai.infrastructure.mq.biz.common.util.ClassLoaderUtil;


public class DefaultMessageProducerManager implements MessageProducerManager {
  private static MessageProducer producer;

  public DefaultMessageProducerManager() {
    if (ClassLoaderUtil.isClassPresent(CatNames.CAT_CLASS)) {
      producer = new CatMessageProducer();
    } else {
      producer = new NullMessageProducerManager().getProducer();
    }
  }

  @Override
  public MessageProducer getProducer() {
    return producer;
  }
}
