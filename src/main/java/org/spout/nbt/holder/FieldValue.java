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
