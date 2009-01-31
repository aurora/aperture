/**
 Gnowsis License 1.0

 Copyright (c) 2004, Leo Sauermann & DFKI German Research Center for Artificial Intelligence GmbH
 All rights reserved.

 This license is compatible with the BSD license http://www.opensource.org/licenses/bsd-license.php

 Redistribution and use in source and binary forms, 
 with or without modification, are permitted provided 
 that the following conditions are met:

 * Redistributions of source code must retain the above copyright notice, 
 this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright notice, 
 this list of conditions and the following disclaimer in the documentation 
 and/or other materials provided with the distribution.
 * Neither the name of the DFKI nor the names of its contributors 
 may be used to endorse or promote products derived from this software 
 without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED 
 WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. 
 IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, 
 INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, 
 PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS 
 INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT 
 LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE 
 OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 endOfLic**/
/**
 * 
 */
package org.semanticdesktop.nepomuk.nrl.inference.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.AbstractList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.Map.Entry;
import java.util.logging.Logger;

/**
 * 
 * @author grimnes
 */
public  class JavaUtils {

	
	/**
	 * Utility class for wrapping String.format calls into normal log calls...
	 * 
	 * @author grimnes
	 */
	public static class FormatLogger extends Logger {

		public FormatLogger(String name) { 
			super(name,null);
		}
		
		protected FormatLogger(String name, String resourceBundleName) {
			super(name, resourceBundleName);
		}

		public void finef(String msg, Object... args) {
			super.fine(String.format(msg,args));
		}

		public void finerf(String msg, Object... args) {
			super.finer(String.format(msg,args));
		}

		public void finestf(String msg, Object... args) {
			super.finest(String.format(msg,args));
		}

		public void infof(String msg, Object... args) {
			super.info(String.format(msg,args));
		}

		public void severef(String msg, Object... args) {
			super.severe(String.format(msg,args));
		}

		public void warningf(String msg, Object... args) {
			super.warning(String.format(msg,args));
		} 
		
		
		
	}
	
	/**
	 * Functional Programming Snippet
	 * A walker can be applied to a list and will be called on each element.
	 *  
	 * @see JavaUtils.walk
	 * @author grimnes
	 * @param <B>
	 */
	public static interface Walker<B> {
		public void visit(B o, int depth);
	}

	/** 
	 * Utility method - used by tree implementation
	 * 
	 * @author grimnes
	 * @param <C>
	 */
	public static class TreeIterator<C> implements Iterator<C> {
		List<Tuple2<C, Integer>> nodes;
		private Iterator<Tuple2<C, Integer>> it;
		private Tuple2<C, Integer> current;
		
		public TreeIterator(Tree<C> tree) {
			nodes=new Vector<Tuple2<C,Integer>>();
			tree.walk(new Walker<C>() {
				public void visit(C o, int d) {
					nodes.add(new Tuple2<C, Integer>(o,d));
				} });
			it=nodes.iterator();
		}

		public boolean hasNext() {
			return it.hasNext();
		}

		public C next() {
			current=it.next();
			return current.one;
		}
		
		/** 
		 * Return the depth of the last element returned by next()
		 * If iteration has not started yet, returns -1 
		 * @return
		 */
		public int depth() { 
			if (current==null) return -1;
			return current.two;
		}

		public void remove() { throw new UnsupportedOperationException(); } 
	}


	/** 
	 * An implementation of an unsorted tree datatype. 
	 * It's slow and sucky, but has few features. 
	 * 
	 * @author grimnes
	 * @param <C>
	 */
	public static class Tree<C> implements Iterable<C> {
		
		C root;
		List<Tree<C>> children;
		
		public Tree(C root) {
			this.root=root;
			children=new Vector<Tree<C>>();
		}
		
		public Tree() {
			children=new Vector<Tree<C>>();
		}
		
		public C getRoot() {
			return root;
		}
		
		public void setRoot(C root) {
			this.root=root;
		}

		public Tree<C> addChild(C child) {
			Tree t = new Tree(child);
			children.add(t);
			return t;
		}
		
		public void addChildren(List<C> newchildren) {
			children.addAll(map(newchildren,new Mapper<Tree<C>,C>() {
				public Tree<C> map(C object) {
					return new Tree<C>(object);
				} }));
		}
		
		public void walk(Walker<C> m) {
			walk(m,0);
		}
		
		private void walk(Walker<C> m, int depth) { 
			m.visit(root,depth);
			for (Tree<C> t: children) {
				t.walk(m,depth+1);
			}
		}
		
		public List<Tree<C>> getChildren() { 
			return children;
		}
		
		public TreeIterator iterator() {
			return new TreeIterator<C>(this);
		}

		public void addChild(Tree<C> tree) {
			children.add(tree);
		}
					
	}
	
	
	
	/** 
	 * a tuple of two elements.
	 * This could maybe be replaced by Map.Entry
	 * @author grimnes
	 * @param <C>
	 * @param <T>
	 */
	public static class Tuple2<C,T> extends AbstractList {
		private C one;
		private T two;

		public Tuple2(C one, T two) {
			this.one=one;
			this.two=two;
		}

		public int size() {
			return 2;
		}

		public Object get(int index) {
			if (index==0) return one; 
			if (index==1) return two;
			throw new IndexOutOfBoundsException();
		}
		
		/** for beans - jstl **/
		public C getOne() { return one; }
		public C one() { return one; }
		
		public T getTwo() { return two; }
		public T two() { return two; }
		
		
	}
	
	/**
	 * Functional Programming Snippet
	 * A Mapper can convert objects of one type to another 
	 * (The types could of course be the same, and it could do any sort of transformation)
	 * @author grimnes
	 */
	public interface Mapper<A,B> {
		public A map(B object);
	}
	
	/**
	 * Functional Programming Snippet
	 * A filter will evaluate objects of a type and return true or false, used to filter collections.
	 * @author grimnes
	 * @param <A>
	 */
	public interface Filter<A> {
		public boolean filter(A object);
	}

	private static SimpleDateFormat dateFormat;
	
	/**
	 * Functional MAP
	 * @param <C>
	 * @param in
	 * @return
	 */
	public static <I extends Object, O extends Object> List<O> map(Collection<I> in, Mapper<O,I> m) {
//		Collection<O> res;
//		try {
//			res = in.getClass().newInstance();
//		} catch (Exception e) {
//			throw new RuntimeException("Could not map",e);
//		}
		List<O> res=new Vector<O>(in.size());
		for (I i:in) res.add(m.map(i));
		return res;
	}
	
	/**
	 * Functional MAP
	 * @param <C>
	 * @param in
	 * @return
	 */
	public static <I extends Object, O extends Object> List<O> map(Iterator<I> in, Mapper<O,I> m) {
		List<O> res=new Vector<O>();
		while(in.hasNext())
			res.add(m.map(in.next()));
		return res;
	}
	
	/**
	 * Functional FILTER
	 * @param <I>
	 * @param in
	 * @param f
	 * @return
	 */
	public static <I extends Object> List<I> filter(Collection<I> in, Filter<I> f) {
		List<I> res=new Vector<I>(in.size());
		for (I i:in) if (f.filter(i)) res.add(i);
		return res;
	}

	/**
	 * Return a list of the string value of each element in the list. 
	 * @param <C>
	 * @param list
	 * @return
	 */
	public static <C extends Object> List<String> listToStrings(Collection<C> list) {
		return (List<String>) map(list,new Mapper<String,C>() {
			public String map(C object) {
				return object.toString();
			} });
	}
	
	/**
	 * Return a list of the string value of each element in the list. 
	 * @param <C>
	 * @param list
	 * @return
	 */
	public static <K extends Object, C extends Object> Map<K,String> valuesToStrings(Map<K,C> list) {
		return mapValues(list,new Mapper<String,C>() {
			public String map(C object) {
				return object.toString();
			} });
	}
	
	/**
	 * Functional MAP
	 * @param <C>
	 * @param in
	 * @return
	 */
	public static <I extends Object, O extends Object, K extends Object> Map<K,O> mapValues(Map<K,I> in, Mapper<O,I> m) {
		Map<K, O> res=new HashMap<K,O>();
		for (Entry<K,I> e: in.entrySet()) {
			res.put(e.getKey(), m.map(e.getValue()));
		}
		return res;
	}
		
	/**
	 * "cast" list of strings to a list of type C,
	 * cons must accept a single string argument
	 * @param <C>
	 * @param list
	 * @return
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws IllegalArgumentException 
	 */
	public static <C extends Object> List<C> stringsToList(List<String> list, Constructor cons) throws IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException {
		List<C> res=new Vector<C>(list.size());
		for (String i: list) {
			res.add((C) cons.newInstance(i));
		}
		return res;
		
	}
	
	/**
	 * "cast" list of strings to a list of type C,
	 * cons must accept a single string argument
	 * @param <C>
	 * @param list
	 * @return
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws IllegalArgumentException 
	 */
	public static <C extends Object> List<C> stringsToList(List<String> list, Method cons) throws IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException {
		List<C> res=new Vector<C>(list.size());
		for (String i: list) {
			res.add((C) cons.invoke(null,i));
		}
		return res;
		
	}
	
	/**
	 * A Map for counting things...
	 *  
	 * @author grimnes
	 * @param <K>
	 * @param <I>
	 */
	public static class Counter <K,I> extends HashMap<K, Integer> { 
		public Counter() { 
			super();
		}
		
		public void inc(K key, int i) {
			put(key,get(key,0)+i);
		}
		
		public void inc(K key) { inc(key,1); }
		
		public Integer get(K key, Integer def) { 
			Integer r=get(key);
			if (r==null) return def;
			return r;
		}
		
		public List<Map.Entry<K,Integer>> topN(int n) {
			Vector<Entry<K, Integer>> res = new Vector<Map.Entry<K,Integer>>();
			res.addAll(entrySet());
			Collections.sort(res, new Comparator<Entry<K, Integer>>() {
				public int compare(Entry<K, Integer> o1, Entry<K, Integer> o2) {
					return o1.getValue().compareTo(o2.getValue());
				}
			});
			
			
			return res.subList(Math.max(res.size()-n,0),res.size());
		}
		
		public int max() {
			return Collections.max(values());
		}
		 
		
	}

	/**
	 * This formats a date, f.x. 2006-08-23T22-24-01 
	 * It's 
	 * @param date
	 * @return
	 */
	public static String dateTime2String(Date date) {
		return getDateFormat().format(date); 
	}
	 
	private static SimpleDateFormat getDateFormat() { 
		if (dateFormat==null) {
			dateFormat=new SimpleDateFormat("yyyy-MM-dd'T'HH-mm-ss");
		} 
		return dateFormat;
	}

	/**
	 * Join the string value of each list element by delim. 
	 * I.e. join([a,b,c],"-") => a-b-c
	 * @param properties
	 * @param string
	 * @return
	 */
	public static <C extends Object> String join(List<C> list, String delim) {
		StringBuffer buffer = new StringBuffer();
		Iterator i=list.iterator();
		while (i.hasNext()) {
			buffer.append(i.next().toString());
			if (i.hasNext()) {
				buffer.append(delim);
			}
		}
		return buffer.toString();

	}

	/**
	 * Returns a map where the items in list are grouped by the value returned by the mapper.
	 * I.e. you can group strings by length and get a map [ 0=>[""], 1=>["a","b"]... ] etc.  
	 * @param list
	 * @param mapper
	 * @return a map
	 */
	public static <A extends Object, B extends Object> Map<A, List<B>> groupBy(List<B> list, Mapper<A, B> mapper) {
		Map<A, List<B>> res=new HashMap<A,List<B>>();
		
		for (B object: list) { 
			A key=mapper.map(object);
			if (!res.containsKey(key)) {
				res.put(key, new Vector<B>());
			}
			res.get(key).add(object);
		}
		
		return res;
		
	}
	
	/**
	 * Returns a map where the items in list are grouped by the value returned by the mapper.
	 * I.e. you can group strings by length and get a map [ 0=>[""], 1=>["a","b"]... ] etc.  
	 * @param list
	 * @param mapper
	 * @return a map
	 */
	public static <A extends Object, B extends Object> Map<A, List<B>> groupBy(B[] list, Mapper<A, B> mapper) {
		Map<A, List<B>> res=new HashMap<A,List<B>>();
		
		for (B object: list) { 
			A key=mapper.map(object);
			if (!res.containsKey(key)) {
				res.put(key, new Vector<B>());
			}
			res.get(key).add(object);
		}
		
		return res;
		
	}
	
	/**
	 * Remove all occurrences of c from start and end of target 
	 * @param target
	 * @param c
	 * @return
	 */
	public static String trim(String target, String c) { 
		return target.replaceAll("^("+c+")*|("+c+")*$", "");
	}

	/** 
	 * return a string with the string s repeated n times
	 * @param s
	 * @param n
	 * @return
	 */
	public static String repeat(String s, int n) {
		StringBuffer res=new StringBuffer(); 
		while (n>0) {
			res.append(s);
			n--;
		}
		return res.toString();
	}

	/** 
	 * Title case the given string
	 * @param s
	 * @return
	 */
	public static String titlecase(String s) {
		return s.substring(0, 1).toUpperCase()+s.substring(1).toLowerCase();
	}
	 
}
