package com.ppdai.infrastructure.mq.biz.common.metric;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.ppdai.infrastructure.mq.biz.common.util.Util;

public class TagName {

	private String name;
	private Map<String, String> tags;

	public TagName(String name, Map<String, String> tags) {
		this.name = name;
		this.tags = tags;
	}

	public String getName() {
		return name;
	}

	public Map<String, String> getTags() {
		return tags;
	}

	public static TagName name(String name) {
		return new TagName(name, new ConcurrentHashMap<>());
	}

	public TagName addTag(String name, String value) {
		if (Util.isEmpty(name) || Util.isEmpty(value)) {
			return this;
		}
		this.tags.put(name, value);
		return this;
	}

	@Override
	public String toString() {
		return TagNameUtil.format(this);
	}
}
