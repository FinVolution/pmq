package com.ppdai.infrastructure.mq.biz.common.util;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;
public class GZipUtil {
	private static final Logger logger = LoggerFactory.getLogger(GZipUtil.class);

	public static String compress(String str) {
		if ((str == null) || (str.isEmpty())) {
			return "";
		}
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			GZIPOutputStream gzip = new GZIPOutputStream(out);
			gzip.write(str.getBytes("UTF-8"));
			gzip.flush();
			gzip.close();
			byte[] tArray = out.toByteArray();
			out.close();
			BASE64Encoder tBase64Encoder = new BASE64Encoder();
			return tBase64Encoder.encode(tArray);
		} catch (Exception ex) {
			logger.error("压缩异常，异常信息：" + ex.getMessage());
		}
		return str;
	}

	public static String uncompress(String value) {
		if ((value == null) || (value.isEmpty())) {
			return "";
		}
		BASE64Decoder tBase64Decoder = new BASE64Decoder();
		try {
			byte[] t = tBase64Decoder.decodeBuffer(value);
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			ByteArrayInputStream in = new ByteArrayInputStream(t);
			GZIPInputStream gunzip = new GZIPInputStream(in);
			try {
				byte[] buffer = new byte[256];
				int n;
				while ((n = gunzip.read(buffer)) >= 0) {					
					out.write(buffer, 0, n);
				}
			} finally {
				gunzip.close();
			}
			in.close();
			out.close();
			return out.toString("UTF-8");
		} catch (Exception ex) {
			logger.error(ex.getMessage());
		}
		return value;
	}
}
