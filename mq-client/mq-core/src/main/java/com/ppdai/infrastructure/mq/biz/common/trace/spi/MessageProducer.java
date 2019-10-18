package com.ppdai.infrastructure.mq.biz.common.trace.spi;


public interface MessageProducer {
  /**
   * Log an error.
   *
   * @param cause root cause exception
   */
  public void logError(Throwable cause);

  /**
   * Log an error.
   *
   * @param cause root cause exception
   */
  public void logError(String message, Throwable cause);

  /**
   * Log an event in one shot with SUCCESS status.
   *
   * @param type event type
   * @param name event name
   */
  public void logEvent(String type, String name);

  /**
   * Log an event in one shot.
   *
   * @param type           event type
   * @param name           event name
   * @param status         "0" means success, otherwise means error code
   * @param nameValuePairs name value pairs in the format of "a=1&b=2&..."
   */
  public void logEvent(String type, String name, String status, String nameValuePairs);

  /**
   * Create a new transaction with given type and name.
   *
   * @param type transaction type
   * @param name transaction name
   */
  public Transaction newTransaction(String type, String name);
  
  public String getDomain();
}
