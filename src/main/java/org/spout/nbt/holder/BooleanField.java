package org.spout.nbt.holder;

import org.spout.nbt.ByteTag;
import org.spout.nbt.Tag;

/**
 * Field to represent a Boolean - These are a bit of a special case in NBT since they're just bytes, which is a PITA
 */
public class BooleanField implements Field<Boolean> {
	public static final BooleanField INSTANCE = new BooleanField();
	public Boolean getValue(Tag<?> tag) throws IllegalArgumentException {
		if (tag instanceof ByteTag) {
			return ((ByteTag) tag).getBooleanValue();
		} else {
			throw new IllegalArgumentException("Expected ByteTag, got " + tag.getClass());
		}
	}

	public Tag<?> getValue(String name, Boolean value) {
		return new ByteTag(name, value);
	}
}
