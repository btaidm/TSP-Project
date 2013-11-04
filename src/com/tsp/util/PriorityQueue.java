package com.tsp.util;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PriorityQueue<K extends Comparable<K>, V> {

	List<SimpleEntry<K, V>> entries;
	
	public PriorityQueue() {
		entries = new ArrayList<SimpleEntry<K, V>>();
	}
	
	public void add(K key, V value) {
		SimpleEntry<K, V> newEntry = new SimpleEntry<K, V>(key, value);
		entries.add(newEntry);
	}
	
	public int size() {
		return entries.size();
	}
	
	public V get(Integer index) {
		return entries.get(index).getValue();
	}
}
