package com.ppdai.infrastructure.mq.biz.service.common;

import java.util.HashMap;
import java.util.Map;

public class MqReadMap<K, V> extends HashMap<K, V> {

	private static final long serialVersionUID = 1L;

	private boolean readOnly = false;

	public MqReadMap(int initialCapacity, float loadFactor) {
		super(initialCapacity, loadFactor);
	}

	public MqReadMap(int initialCapacity) {
		super(initialCapacity);
	}

	public MqReadMap() {
	}

	public MqReadMap(Map<? extends K, ? extends V> m) {
		super(m);
	}

	@Override
	public V put(K key, V value) {
		if (!readOnly) {
			return super.put(key, value);
		} else {
			throw new RuntimeException("对象只读，无法修改！");
		}
	}

	@Override
	public V remove(Object key) {
		if (!readOnly) {
			return super.remove(key);
		} else {
			throw new RuntimeException("对象只读，无法修改！");
		}
	}
	
	@Override
	public void putAll(Map<? extends K, ? extends V> m){
		if (!readOnly) {
			 super.putAll(m);
		} else {
			throw new RuntimeException("对象只读，无法修改！");
		}
	}
	
	@Override
	public void clear(){
		if (!readOnly) {
			 super.clear();
		} else {
			throw new RuntimeException("对象只读，无法修改！");
		}
	}
	
	public void setOnlyRead(){
		readOnly=true;
	}
}
