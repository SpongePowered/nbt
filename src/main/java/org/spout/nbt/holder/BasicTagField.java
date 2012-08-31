/*
 * This file is part of SimpleNBT.
 *
 * Copyright (c) 2011, SpoutDev <http://www.spout.org/>
 * SimpleNBT is licensed under the SpoutDev License Version 1.
 *
 * SimpleNBT is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the SpoutDev License Version 1.
 *
 * SimpleNBT is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License,
 * the MIT license and the SpoutDev License Version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://www.spout.org/SpoutDevLicenseV1.txt> for the full license,
 * including the MIT license.
 */
package org.spout.nbt.holder;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import org.spout.nbt.ByteTag;
import org.spout.nbt.Tag;

/**
 * Represents a field containing a basic tag type
 */
public class BasicTagField<T> implements Field<T> {
	private static final Map<Class<? extends Tag<?>>, Constructor<Tag<?>>> CONSTRUCTOR_CACHE = new HashMap<Class<? extends Tag<?>>, Constructor<Tag<?>>>();
	static {
		try {
			//noinspection unchecked
			CONSTRUCTOR_CACHE.put(ByteTag.class, (Constructor) ByteTag.class.getConstructor(String.class, byte.class)); // ByteTag has a constructor that takes a boolean too, we don't want to use that
		} catch (NoSuchMethodException e) {
			throw new ExceptionInInitializerError(e);
		}
	}
	private final Class<? extends Tag<T>> valueType;

	public BasicTagField(Class<? extends Tag<T>> valueType) {
		this.valueType = valueType;
	}

	public T getValue(Tag<?> tag) throws IllegalArgumentException {
		if (!valueType.isAssignableFrom(tag.getClass())) {
			throw new IllegalArgumentException("Tag is not of type " + valueType);
		}
		Tag<T> value = valueType.cast(tag);
		return value.getValue();
	}

	public Tag<T> getValue(String name, T value) {
		Constructor<Tag<T>> constr = getConstructor(valueType);
		constr.setAccessible(true);
		try {
			return constr.newInstance(name, value);
		} catch (InstantiationException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException ignore) { // Should not happen, we set accessible to true
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	private static <T> Constructor<Tag<T>> getConstructor(Class<? extends Tag<T>> tag) {
		// WARNING: Java generics suck, ugly code ahead
		Constructor<Tag<T>> constructor = (Constructor) CONSTRUCTOR_CACHE.get(tag);
		if (constructor == null) {
			Constructor<?>[] constructors = tag.getConstructors();
			if (constructors.length == 1
					&& constructors[0].getParameterTypes().length == 2
					&& String.class.isAssignableFrom(constructors[0].getParameterTypes()[0])) {
				constructor = (Constructor<Tag<T>>) constructors[0];
				CONSTRUCTOR_CACHE.put(tag, (Constructor) constructor);
			} else {
				throw new IllegalArgumentException(tag + " does not have one constructor with the correct type!");
			}
		}
		return constructor;
	}
}
