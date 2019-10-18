package com.ppdai.infrastructure.mq.biz.common.util;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Util {

	private final static String DEFAULT_FORMATE = "yyyy-MM-dd HH:mm:ss";
	public final static String  SSS_FORMATE= "yyyy-MM-dd HH:mm:ss:SSS";

	/**
	 * 获取进程Id
	 *
	 * @return
	 */
	public static Integer getProcessId() {
		RuntimeMXBean runtime = ManagementFactory.getRuntimeMXBean();
		String name = runtime.getName();
		return Integer.parseInt(name.substring(0, name.indexOf("@")));
	}

	public static String formateDate(Date date, String formate) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(formate);
		return simpleDateFormat.format(date);
	}

	public static String formateDate(Date date) {
		try {
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DEFAULT_FORMATE);
			return simpleDateFormat.format(date);
		} catch (Exception e) {
			return null;
		}
	}

	public static void sleep(long millisecondes) {
		try {
			TimeUnit.MILLISECONDS.sleep(millisecondes);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
		}
	}

	// 列表分组
	public static <T> List<List<T>> split(List<T> lst, int count) {
		List<List<T>> lstRs = new ArrayList<>();
		List<T> countLst = new ArrayList<>(count);
		for (T t : lst) {
			if (countLst.size() == count) {
				lstRs.add(countLst);
				countLst = new ArrayList<>(count);
			}
			countLst.add(t);
		}
		if (countLst.size() > 0) {
			lstRs.add(countLst);
		}
		return lstRs;

	}	
	public static boolean isEmpty(String str) {
		return (str == null || "".equals(str)||str.trim().length()==0);
	}
}
