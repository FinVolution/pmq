package com.ppdai.infrastructure.mq.biz.common.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class ClassLoaderUtilTest {

	@Test
	public void getLoaderTest() {
		assertEquals(true, ClassLoaderUtil.getLoader() != null);
	}
	@Test
	public void getClassPathTest() {
		assertEquals(true, ClassLoaderUtil.getClassPath()!=null);
	}
	
	@Test
	public void isClassPresentTest() {
		assertEquals(false, ClassLoaderUtil.isClassPresent("tt.tt"));
		
		assertEquals(true, ClassLoaderUtil.isClassPresent("com.ppdai.infrastructure.mq.biz.common.util.ClassLoaderUtilTest"));
	}
}
