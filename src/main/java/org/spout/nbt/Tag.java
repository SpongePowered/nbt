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
package org.spout.nbt;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Represents a single NBT tag.
 */
public abstract class Tag<T> implements Comparable<Tag<?>> {
	/**
	 * The name of this tag.
	 */
	private final String name;

	private final TagType type;

	/**
	 * Creates the tag with no name.
	 */
	public Tag(TagType type) {
		this(type, "");
	}

	/**
	 * Creates the tag with the specified name.
	 * @param name The name.
	 */
	public Tag(TagType type, String name) {
		this.name = name;
		this.type = type;
	}

	/**
	 * Gets the name of this tag.
	 * @return The name of this tag.
	 */
	public final String getName() {
		return name;
	}

	/**
	 * Returns the type of this tag
	 *
	 * @return The type of this tag.
	 */
	public TagType getType() {
		return type;
	}

	/**
	 * Gets the value of this tag.
	 * @return The value of this tag.
	 */
	public abstract T getValue();

	/**
	 * Clones a Map<String, Tag>
	 *
	 * @param map the map
	 * @return a clone of the map
	 */
	public static Map<String, Tag<?>> cloneMap(Map<String, Tag<?>> map) {
		if (map == null) {
			return null;
		}

		Map<String, Tag<?>> newMap = new HashMap<String, Tag<?>>();
		for (Entry<String, Tag<?>> entry : map.entrySet()) {
			newMap.put(entry.getKey(), entry.getValue().clone());
		}
		return newMap;
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof Tag)) {
			return false;
		}
		Tag<?> tag = (Tag<?>) other;
		return getValue().equals(tag.getValue()) && getName().equals(tag.getName());
	}

	@Override
	public int compareTo(Tag other) {
		if (equals(other)) {
			return 0;
		} else {
			if (other.getName().equals(getName())) {
				throw new IllegalStateException("Cannot compare two Tags with the same name but different values for sorting");
			} else {
				return getName().compareTo(other.getName());
			}
		}
	}

	/**
	 * Clones the Tag
	 *
	 * @return the clone
	 */
	public abstract Tag<T> clone();
}
