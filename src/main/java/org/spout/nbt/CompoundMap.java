package org.spout.nbt;

import java.util.HashMap;

public class CompoundMap extends HashMap<String, Tag> {

	public CompoundMap(CompoundMap value) {
		super(value);
	}
	
	public CompoundMap(HashMap<String, Tag> value) {
		super(value);
	}

	public CompoundMap() {
		super();
	}

	/**
	 * Puts a tag in this map, getting the string from the tag
	 * 
	 * @param tag to add
	 * @return the previous value
	 */
	public Tag put(Tag tag) {
		return super.put(tag.getName(), tag);
	}
}
