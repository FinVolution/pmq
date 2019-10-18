package com.ppdai.infrastructure.mq.biz.common.metric;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Metric {
	private String name;
	private Map<String, String> tags;
	private Object value;
	private long timestamp;

	public Metric(String name) {
		this.name = name;
		this.tags = new HashMap<>();
		this.value = Integer.valueOf(0);
		this.timestamp = System.currentTimeMillis();
	}

	public Metric(String name, Map<String, String> tags, long timeStamp, Object value) {
		this.name = name;
		this.tags = tags;
		this.timestamp = timeStamp;
		this.value = value;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Map<String, String> getTags() {
		return this.tags;
	}

	public void setTags(Map<String, String> tags) {
		this.tags = tags;
	}

	public void addTag(String key, String value) {
		if (this.tags == null) {
			this.tags = new HashMap<>();
		}

		this.tags.put(key, value);
	}

	public Object getValue() {
		return this.value;
	}

	public void setValue(Object values) {
		this.value = values;
	}

	public long getTimestamp() {
		return this.timestamp;
	}

	public void setTimestamp(long timeStamp) {
		this.timestamp = timeStamp;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("name:").append(this.name).append("\n").append("tag:").append(this.tags.toString()).append("\n")
				.append("value:").append(this.value).append("\n");
		return sb.toString();
	}

	public int hashCode() {
		return Arrays.hashCode(new Object[]{this.name, Long.valueOf(this.timestamp), this.value, this.tags});
	}

	public boolean equals(Object o) {
		if (o == this) {
			return true;
		} else if (!(o instanceof Metric)) {
			return false;
		} else {
			Metric rhs = (Metric) o;
			return this.equals(this.name, rhs.name)
					&& this.equals(Long.valueOf(this.timestamp), Long.valueOf(rhs.timestamp))
					&& this.equals(this.value, rhs.value) && this.equals(this.tags, rhs.tags);
		}
	}

	private boolean equals(Object a, Object b) {
		return a == b || a != null && a.equals(b);
	}
}