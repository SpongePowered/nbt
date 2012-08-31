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

/**
 * A class which contains NBT-related utility methods.
 */
public final class NBTUtils {
	/**
	 * Gets the type name of a tag.
	 *
	 * @param clazz The tag class.
	 * @return The type name.
	 */
	@Deprecated
	public static String getTypeName(Class<? extends Tag<?>> clazz) {
		return TagType.getByTagClass(clazz).getTypeName();
	}

	/**
	 * Gets the type code of a tag class.
	 *
	 * @param clazz The tag class.
	 * @return The type code.
	 * @throws IllegalArgumentException if the tag class is invalid.
	 */
	@Deprecated
	public static int getTypeCode(Class<? extends Tag<?>> clazz) {
		return TagType.getByTagClass(clazz).getId();
	}

	/**
	 * Gets the class of a type of tag.
	 *
	 * @param type The type.
	 * @return The class.
	 * @throws IllegalArgumentException if the tag type is invalid.
	 */
	@Deprecated
	public static Class<? extends Tag> getTypeClass(int type) {
		return TagType.getById(type).getTagClass();
	}

	/**
	 * Default private constructor.
	 */
	private NBTUtils() {
	}
}
