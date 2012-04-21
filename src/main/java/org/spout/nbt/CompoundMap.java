package org.spout.nbt;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class CompoundMap implements Map<String, Tag>, Iterable<Tag> {
	
	private final Map<String, Tag> map;
	private final boolean preserveOrder;
	private final boolean sort;
	private final boolean reverse;
	
	/**
	 * Creates an empty CompoundMap backed by a HashMap.
	 */
	public CompoundMap() {
		this(null, false, false, false);
	}
	
	/**
	 * Creates a CompoundMap back by a LinkedHashMap, so insertion order is preserved.<br>
	 * <br>
	 * The map is initialised using the values given in the List.
	 * 
	 * @param initial the initial values for the CompoundMap 
	 */
	public CompoundMap(List<Tag> initial) {
		this(initial, true, false, false);
	}
	
	/**
	 * Creates a CompoundMap back by a HashMap, so element ordering is not defined.<br>
	 * <br>
	 * The map is initialised using the values given in the HashMap.
	 * 
	 * @param initial the initial values for the CompoundMap 
	 */
	public CompoundMap(HashMap<String, Tag> initial) {
		this(initial.values(), false, false, false);
	}

	/**
	 * Creates a CompoundMap using the same element ordering rules as in the given CompoundMap.<br>
	 * <br>
	 * The map is initialised using the values given in the CompoundMap.
	 * 
	 * @param initial the initial values for the CompoundMap 
	 */
	public CompoundMap(CompoundMap initial) {
		this(initial.values(), initial.preserveOrder, initial.sort, initial.reverse);
	}
	
	/**
	 * Creates an empty CompoundMap.<br>
	 * <br>
	 * 
	 * @param perserveOrder elements are ordered in insertion order
	 * @param sort elements are ordered in alphabetical ordering
	 * @param reverse elements are ordered in reverse alphabetical ordering
	 */
	public CompoundMap(boolean preserveOrder, boolean sort, boolean reverse) {
		this(null, preserveOrder, sort, reverse);
	}

	/**
	 * Creates an empty CompoundMap which is initialised using the given values<br>
	 * <br>
	 * 
	 * @param initial the initial values
	 * @param perserveOrder elements are ordered in insertion order
	 * @param sort elements are ordered in alphabetical ordering
	 * @param reverse elements are ordered in reverse alphabetical ordering
	 */
	public CompoundMap(Iterable<Tag> initial, boolean preserveOrder, boolean sort, boolean reverse) {
		if (preserveOrder && sort) {
			throw new IllegalArgumentException("A compound map cannot both preserve order and sort the keys");
		}
		if (reverse) {
			this.sort = true;
		} else {
			this.sort = sort;
		}
		this.preserveOrder = preserveOrder;
		this.reverse = reverse;
		if (preserveOrder) {
			this.map = new LinkedHashMap<String, Tag>();
		} else if (sort) {
			if (reverse) {
				this.map = new TreeMap<String, Tag>(Collections.reverseOrder());
			} else {
				this.map = new TreeMap<String, Tag>();
			}
		} else {
			this.map = new HashMap<String, Tag>();
		}
		if (initial != null) {
			for (Tag t : initial) {
				put(t);
			}
		}
	}
	
	/**
	 * Puts a tag in this map, getting the string from the tag
	 * 
	 * @param tag to add
	 * @return the previous value
	 */
	public Tag put(Tag tag) {
		return map.put(tag.getName(), tag);
	}

	@Override
	public void clear() {
		map.clear();
	}

	@Override
	public boolean containsKey(Object key) {
		return map.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		return map.containsValue(value);
	}

	@Override
	public Set<Map.Entry<String, Tag>> entrySet() {
		return map.entrySet();
	}

	@Override
	public Tag get(Object key) {
		return map.get(key);
	}

	@Override
	public boolean isEmpty() {
		return map.isEmpty();
	}

	@Override
	public Set<String> keySet() {
		return map.keySet();
	}

	@Override
	public Tag put(String key, Tag value) {
		return map.put(key, value);
	}

	@Override
	public void putAll(Map<? extends String, ? extends Tag> values) {
		map.putAll(values);
	}

	@Override
	public Tag remove(Object key) {
		return map.remove(key);
	}

	@Override
	public int size() {
		return map.size();
	}

	@Override
	public Collection<Tag> values() {
		return map.values();
	}

	@Override
	public Iterator<Tag> iterator() {
		return values().iterator();
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof CompoundMap) {
			CompoundMap objMap = (CompoundMap) obj;
			return map.equals(objMap.map);
		}
		return false;
	}
}
