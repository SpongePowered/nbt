/*
 * This file is part of SimpleNBT.
 *
 * Copyright (c) 2011 Spout LLC <http://www.spout.org/>
 * SimpleNBT is licensed under the Spout License Version 1.
 *
 * SimpleNBT is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the Spout License Version 1.
 *
 * SimpleNBT is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for
 * more details.
 *
 * You should have received a copy of the GNU Lesser General Public License,
 * the MIT license and the Spout License Version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://spout.in/licensev1> for the full license, including
 * the MIT license.
 */
package org.spout.nbt.holder;

import org.spout.nbt.Tag;

/**
 * Utility classes for field handling. Idiot java doesn't allow concrete static methods in interfaces.
 */
public class FieldUtils {
	private FieldUtils() {
	}

	/**
	 * Checks that a tag is not null and of the required type
	 *
	 * @param tag The tag to check
	 * @param type The type of tag required
	 * @param <T> The type parameter of {@code type}
	 * @return The casted tag
	 * @throws IllegalArgumentException if the tag is null or not of the required type
	 */
	public static <T extends Tag<?>> T checkTagCast(Tag<?> tag, Class<T> type) throws IllegalArgumentException {
		if (tag == null) {
			throw new IllegalArgumentException("Expected tag of type " + type.getName() + ", was null");
		} else if (!type.isInstance(tag)) {
			throw new IllegalArgumentException("Expected tag to be a " + type.getName() + ", was a " + tag.getClass().getName());
		}
		return type.cast(tag);
	}
}
