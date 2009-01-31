/**
 * 
 */
package org.semanticdesktop.nepomuk.nrl.inference.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 
 * A Map like structure where each key maps to a list of values. 
 * I couldn't get the generics to work the way I wanted, so it doesn't inherit anything.
 * TODO: move this to a utils project. 
 * 
 * @author grimnes
 *
 */
public class ListMap<K,V> {

	private HashMap<K, List<V>> data;

	public ListMap() { 
		data=new HashMap<K,List<V>>();
	}
	
	public V add(K key, V value) {
		List<V> list;
		if (data.containsKey(key)) {
			list=data.get(key);
		} else { 
			list=new ArrayList<V>();
		}
		list.add(value);
		data.put(key, list);
		return null;
	}

	public List<V>get(K key) {
		return data.get(key);
	}

	public boolean containsKey(K key) {
		return data.containsKey(key);
	}

	@Override
	public String toString() {
		return data.toString();
	}

	public void remove(K key, V value) {
		try { 
			data.get(key).remove(value);
			if (data.get(key).size()==0) data.remove(key);
		} catch(NullPointerException e) {
			// ignore 
		}
	}
	
	
}
