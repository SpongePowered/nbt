package org.spout.nbt.holder;

import org.spout.nbt.Tag;

/**
 * Base field in NBT serialization
 */
public interface Field<T> {
	/**
	 * Get the value of this field from the given tag
	 * @param tag The tag to use
	 * @return The value
	 * @throws IllegalArgumentException when the tag is of the wrong type
	 */
	public T getValue(Tag<?> tag) throws IllegalArgumentException;

	/**
	 * Convert a value to its serialized NBT form
	 * @param name The key to use for the NBT tag
	 * @param value The value
	 * @return The value serialized to a tag
	 */
	public Tag<?> getValue(String name, T value);
}
