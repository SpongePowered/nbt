package org.spout.nbt.holder;

import org.spout.nbt.CompoundMap;
import org.spout.nbt.CompoundTag;
import org.spout.nbt.Tag;

/**
 * Represents the value of a field
 */
public class FieldValue<T> {
	private T value;
	private final Field<T> field;
	private final String key;
	private final T defaultValue;

	public FieldValue(String key, Field<T> field) {
		this(key, field, null);
	}

	public FieldValue(String key, Field<T> field, T defaultValue) {
		this.field = field;
		this.key = key;
		this.defaultValue = defaultValue;
	}

	/**
	 * Get this field from a CompoundTag
	 * @param tag The tag to get this field from
	 * @return The value
	 */
	public T load(CompoundTag tag) {
		Tag subTag = tag.getValue().get(key);
		if (subTag == null) {
			return (value = defaultValue);
		}
		return (value = field.getValue(subTag));
	}

	public void save(CompoundMap tag) {
		T value = this.value;
		if (value == null) {
			if ((value = defaultValue) == null) {
				return;
			}
		}
		Tag t = field.getValue(key, value);
		tag.put(t);
	}

	public T get() {
		return value;
	}

	public void set(T value) {
		this.value = value;
	}

	// So generic info doesn't have to be duplicated
	public static <T> FieldValue<T> from(String name, Field<T> field, T defaultValue) {
		return new FieldValue<T>(name, field, defaultValue);
	}

	public static <T> FieldValue<T> from(String name, Field<T> field) {
		return new FieldValue<T>(name, field);
	}
}
