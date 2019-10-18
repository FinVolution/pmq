package com.ppdai.infrastructure.mq.biz.common.trace;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.ppdai.infrastructure.mq.biz.common.util.Util;

/**
 * 保留追踪信息 Created by liujianjun02 on 2017/12/15.
 */
public class TraceMessage {
	// private volatile AtomicInteger counter = new AtomicInteger(0);
	private volatile int counter = 0;
	private volatile TraceMessageItem[] data = new TraceMessageItem[100];
	private String name;
	private transient ReentrantReadWriteLock tReadWriteLock = new ReentrantReadWriteLock(true);

	public TraceMessage(String name) {
		this.name = name;
	}

	public void add(TraceMessageItem traceMessageItem) {
		if (!TraceFactory.isEnabled(name)) {
			return;
		}
		if (Util.isEmpty(traceMessageItem.status)) {
			traceMessageItem.status = "none";
		}
		traceMessageItem.endTime = Util.formateDate(new Date(), Util.SSS_FORMATE);		
		doAdd(traceMessageItem);
	}

	private void doAdd(TraceMessageItem traceMessageItem) {
		try {
			TraceMessageItem preTraceMessage = null;
			tReadWriteLock.readLock().lock();
			if (counter > 99) {
				counter = 0;
			}
			preTraceMessage = data[counter];
			tReadWriteLock.readLock().unlock();
			if (preTraceMessage != null) {
				if (preTraceMessage.status.equals(traceMessageItem.status)) {
					preTraceMessage.startTime = traceMessageItem.startTime;
					preTraceMessage.endTime = traceMessageItem.endTime;
					preTraceMessage.msg = traceMessageItem.msg;
				} else {
					traceMessageItem.endTime = traceMessageItem.endTime;
					tReadWriteLock.writeLock().lock();
					counter++;
					if (counter > 99) {
						counter = 0;
					}
					data[counter] = traceMessageItem;
					tReadWriteLock.writeLock().unlock();
				}

			} else {
				data[counter] = traceMessageItem;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Map<Integer, TraceMessageItem> getData() {
		Map<Integer, TraceMessageItem> traceMessageItems = new HashMap<>(100);
		for (int t = 0; t < 100; t++) {
			if (data[t] != null) {
				traceMessageItems.put(t, data[t]);
			} else {
				break;
			}
		}
		return traceMessageItems;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getCounter() {
		return counter;
	}

	public void setCounter(int counter) {
		this.counter = counter;
	}
}
