package com.ppdai.infrastructure.mq.biz.common.util;

import java.text.SimpleDateFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class JsonUtil {

	private static Logger log = LoggerFactory.getLogger(JsonUtil.class);
	private static ObjectMapper objectMapper;
	static {
		objectMapper = new ObjectMapper();
		objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS"));
		// objectMapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
		objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,false);
	}

	public static ObjectMapper getObjMapper() {
		return objectMapper;
	}

	public static String toJson(Object obj) {
		if (obj == null) {
			return "";
		}
		try {
			return objectMapper.writeValueAsString(obj);
			// return JSON.toJSONString(obj, SerializerFeature.PrettyFormat,
			// SerializerFeature.WriteDateUseDateFormat);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.error("tojson 异常", e);
			throw new RuntimeException(e);
		}
	}

	public static String toJsonNull(Object obj) {
		if (obj == null) {
			return "";
		}
		try {
			return objectMapper.writeValueAsString(obj);
			// return JSON.toJSONString(obj, SerializerFeature.PrettyFormat,
			// SerializerFeature.WriteDateUseDateFormat);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.error("toJsonNull 异常", e);
			return "";
		}
	}
	//new TypeReference<Response<List<User>>>(){}
	public static <Result> Result parseJson(String json, Class<Result> t) {
		if (isEmpty(json)) {
			return null;
		}
		try {
			// return JSON.parseObject(json, t);
			return objectMapper.readValue(json, t);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.error("tojson 异常", e);
			throw new RuntimeException(e);
		}
	}
	
	public static <Result> Result copy(Object obj, Class<Result> t) {
		if (obj==null) {
			return null;
		}
		try {
			// return JSON.parseObject(json, t);
			return parseJson(toJsonNull(obj),t);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.error("copy 异常", e);
			throw new RuntimeException(e);
		}
	}
	
	public static <Result> Result copy(Object obj, TypeReference<Result> t) {
		if (obj==null) {
			return null;
		}
		try {
			// return JSON.parseObject(json, t);
			return parseJson(toJsonNull(obj),t);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.error("copy 异常", e);
			throw new RuntimeException(e);
		}
	}

	public static <Result> Result parseJson(String json, TypeReference<Result> t) {
		if (isEmpty(json)) {
			return null;
		}
		try {
			// return JSON.parseObject(json, t);
			return objectMapper.readValue(json, t);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.error("tojson 异常", e);
			//throw new RuntimeException(e);
			return null;
		}
	}

	private static boolean isEmpty(String value) {
		return value == null || value.length() == 0 || value.trim().length() == 0;
	}
	
}
