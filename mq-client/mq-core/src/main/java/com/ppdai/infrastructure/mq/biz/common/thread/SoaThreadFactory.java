package com.ppdai.infrastructure.mq.biz.common.thread;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SoaThreadFactory implements ThreadFactory {
	private static Logger log = LoggerFactory.getLogger(SoaThreadFactory.class);

	private final AtomicLong threadNumber = new AtomicLong(1);

	private final String namePrefix;
	private int priority = 0;

	private final boolean daemon;

	private static final ThreadGroup THREAD_GROUP = new ThreadGroup("Mq");

	public static ThreadGroup getThreadGroup() {
		return THREAD_GROUP;
	}

	public static ThreadFactory create(String namePrefix, boolean daemon) {
		return new SoaThreadFactory(namePrefix, daemon);
	}

	public static ThreadFactory create(String namePrefix, int priority, boolean daemon) {
		return new SoaThreadFactory(namePrefix, priority, daemon);
	}

	public static boolean waitAllShutdown(int timeoutInMillis) {
		ThreadGroup group = getThreadGroup();
		Thread[] activeThreads = new Thread[group.activeCount()];
		group.enumerate(activeThreads);
		Set<Thread> alives = new HashSet<Thread>(Arrays.asList(activeThreads));
		Set<Thread> dies = new HashSet<Thread>();
		log.info("Current ACTIVE thread count is: {}", alives.size());
		long expire = System.currentTimeMillis() + timeoutInMillis;
		while (System.currentTimeMillis() < expire) {
			classify(alives, dies, new ClassifyStandard<Thread>() {
				@Override
				public boolean satisfy(Thread thread) {
					return !thread.isAlive() || thread.isInterrupted() || thread.isDaemon();
				}
			});
			if (alives.size() > 0) {
				log.info("Alive radar threads: {}", alives);
				try {
					TimeUnit.SECONDS.sleep(2);
				} catch (InterruptedException ex) {
					// ignore
				}
			} else {
				log.info("All radar threads are shutdown.");
				return true;
			}
		}
		log.warn("Some radar threads are still alive but expire time has reached, alive threads: {}", alives);
		return false;
	}

	private static interface ClassifyStandard<T> {
		boolean satisfy(T thread);
	}

	private static <T> void classify(Set<T> src, Set<T> des, ClassifyStandard<T> standard) {
		Set<T> set = new HashSet<>();
		for (T t : src) {
			if (standard.satisfy(t)) {
				set.add(t);
			}
		}
		src.removeAll(set);
		des.addAll(set);
	}

	private SoaThreadFactory(String namePrefix, boolean daemon) {
		this.namePrefix = namePrefix;
		this.daemon = daemon;
	}

	private SoaThreadFactory(String namePrefix, int priority, boolean daemon) {
		this.namePrefix = namePrefix;
		this.daemon = daemon;
		this.priority = priority;
	}

	@Override
	public Thread newThread(Runnable runnable) {
		Thread thread = new Thread(THREAD_GROUP, runnable, //
				THREAD_GROUP.getName() + "-" + namePrefix + "-" + threadNumber.getAndIncrement());
		thread.setDaemon(daemon);
		//ThreadRd.addTh(THREAD_GROUP.getName() + "-" + namePrefix);
		if (priority > 0) {
			thread.setPriority(priority);
		}
		// if (thread.getPriority() != Thread.NORM_PRIORITY) {
		// thread.setPriority(Thread.NORM_PRIORITY);
		// }
		return thread;
	}

//	public static class ThreadRd {
//		private static Map<String, String> mp = new ConcurrentHashMap<>();
//
//		public static  void addTh(String t) {
//			if (mp.containsKey(t)) {
//				System.out.println(t);
//			} else {
//				mp.put(t, "");
//			}
//		}
//	}
}
