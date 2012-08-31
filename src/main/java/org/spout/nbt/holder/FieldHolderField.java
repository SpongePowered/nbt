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
