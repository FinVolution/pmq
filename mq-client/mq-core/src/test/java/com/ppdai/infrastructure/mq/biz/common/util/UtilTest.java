package com.ppdai.infrastructure.mq.biz.common.util;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class UtilTest {

	@Test
	public void getProcessIdTest() {
		assertEquals(true, Util.getProcessId() > 0);
	}

	@Test
	public void formateDateTest() {
		Date date = new Date(2019 - 1900, 0, 1);
		Calendar calendar = Calendar.getInstance();
		calendar.set(2019, 0, 1, 0, 0, 0);
		assertEquals("2019-01-01", Util.formateDate(calendar.getTime(), "yyyy-MM-dd"));
		assertEquals("2019-01-01 00:00:00", Util.formateDate(calendar.getTime()));
		assertEquals("2019-01-01", Util.formateDate(date, "yyyy-MM-dd"));
		assertEquals("2019-01-01 00:00:00", Util.formateDate(date));
	}

	@Test
	public void sleepTest() {
		Util.sleep(1);
	}

	@Test
	public void splitTest() {
		List<Integer> data = Arrays.asList(1, 2, 3, 4, 5, 6);
		assertEquals(3, Util.split(data, 2).size());
		assertEquals(2, Util.split(data, 3).size());
		assertEquals(2, Util.split(data, 4).size());
		data = new ArrayList<Integer>();
		assertEquals(0, Util.split(data, 1).size());
	}

	@Test
	public void isEmptyTest() {
		assertEquals(false, Util.isEmpty("fasdf"));
		assertEquals(true, Util.isEmpty(""));
		assertEquals(true, Util.isEmpty(null));
	}
}
