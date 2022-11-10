package com.bct.HOS.App.utils;

import java.util.HashMap;
import java.util.Map;

public class InMem implements Cache<Object, Object>{

	private Map<Object, Object> map;

	private static InMem mem = new InMem();

	public static InMem getInstance() {
		return mem;
	}

	public InMem() {
		map = new HashMap<Object, Object>();
	}

	public void put(Object key, Object value) {
		// TODO Auto-generated method stub
		map.put(key, value);
	}

	public Object get(Object key) {
		// TODO Auto-generated method stub
		return map.get(key);
	}

}
