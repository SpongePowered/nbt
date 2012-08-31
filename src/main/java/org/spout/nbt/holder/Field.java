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
