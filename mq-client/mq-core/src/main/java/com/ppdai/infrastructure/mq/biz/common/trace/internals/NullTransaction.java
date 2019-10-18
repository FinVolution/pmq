package com.ppdai.infrastructure.mq.biz.common.trace.internals;

import com.ppdai.infrastructure.mq.biz.common.trace.spi.Transaction;


public class NullTransaction implements Transaction {
  @Override
  public void setStatus(String status) {
  }

  @Override
  public void setStatus(Throwable e) {
  }

  @Override
  public void addData(String key, Object value) {
  }

  @Override
  public void complete() {
  }
}
