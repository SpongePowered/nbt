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

import java.nio.charset.Charset;

/**
 * A class which holds constant values.
 */
public final class NBTConstants {
	/**
	 * The character set used by NBT (UTF-8).
	 */
	public static final Charset CHARSET = Charset.forName("UTF-8");

	/**
	 * Tag type constants.
	 */
	@Deprecated
	public static final int TYPE_END = TagType.TAG_END.getId(),
		TYPE_BYTE = TagType.TAG_BYTE.getId(),
		TYPE_SHORT = TagType.TAG_SHORT.getId(),
		TYPE_INT = TagType.TAG_INT.getId(),
		TYPE_LONG = TagType.TAG_LONG.getId(),
		TYPE_FLOAT = TagType.TAG_FLOAT.getId(),
		TYPE_DOUBLE = TagType.TAG_DOUBLE.getId(),
		TYPE_BYTE_ARRAY = TagType.TAG_BYTE_ARRAY.getId(),
		TYPE_STRING = TagType.TAG_STRING.getId(),
		TYPE_LIST = TagType.TAG_LIST.getId(),
		TYPE_COMPOUND = TagType.TAG_COMPOUND.getId(),
		TYPE_INT_ARRAY = TagType.TAG_INT_ARRAY.getId(),
		TYPE_SHORT_ARRAY = TagType.TAG_SHORT_ARRAY.getId();

	/**
	 * Default private constructor.
	 */
	private NBTConstants() {
	}
}
