package org.spout.nbt.holder;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.spout.nbt.CompoundTag;
import org.spout.nbt.Tag;

/**
 * A field that holds the contents of a FieldHolder
 */
public class FieldHolderField<T extends FieldHolder> implements Field<T> {
	private final Class<T> type;
	private final Constructor<T> typeConst;

	public FieldHolderField(Class<T> type) {
		this.type = type;
		try {
			typeConst = type.getConstructor();
			typeConst.setAccessible(true);
		} catch (NoSuchMethodException e) {
			throw new ExceptionInInitializerError("Type must have zero-arg constructor!");
		}
	}

	public T getValue(Tag<?> tag) throws IllegalArgumentException {
		if (!(tag instanceof CompoundTag)) {
			throw new IllegalArgumentException("Expected tag to be a CompoundTag, was a " + tag.getClass());
		}

		T value = null;
		try {
			value = typeConst.newInstance();
			value.load((CompoundTag) tag);
		} catch (InstantiationException e) {
		} catch (IllegalAccessException e) {
		} catch (InvocationTargetException e) {
		}
		return value;
	}

	public Tag<?> getValue(String name, T value) {
		return new CompoundTag(name, value.save());
	}
}
