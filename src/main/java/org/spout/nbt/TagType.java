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

/**
 * @author zml2008
 */
public enum TagType {
	TAG_END(EndTag.class, "TAG_End", 0),
	TAG_BYTE(ByteTag.class, "TAG_Byte", 1),
	TAG_SHORT(ShortTag.class, "TAG_Short", 2),
	TAG_INT(IntTag.class, "TAG_Int", 3),
	TAG_LONG(LongTag.class, "TAG_Long", 4),
	TAG_FLOAT(FloatTag.class, "TAG_Float", 5),
	TAG_DOUBLE(DoubleTag.class, "TAG_Double", 6),
	TAG_BYTE_ARRAY(ByteArrayTag.class, "TAG_Byte_Array", 7),
	TAG_STRING(StringTag.class, "TAG_String", 8),
	TAG_LIST((Class) ListTag.class, "TAG_List", 9), // Java generics, y u so suck
	TAG_COMPOUND(CompoundTag.class, "TAG_Compound", 10),
	TAG_INT_ARRAY(IntArrayTag.class, "TAG_Int_Array", 11),
	TAG_SHORT_ARRAY(ShortArrayTag.class, "TAG_Short_Array", 100),
	;

	private static final Map<Class<? extends Tag<?>>, TagType> BY_CLASS = new HashMap<Class<? extends Tag<?>>, TagType>();
	private static final Map<String, TagType> BY_NAME = new HashMap<String, TagType>();
	private static final TagType[] BY_ID;
	static {
		BY_ID = new TagType[BaseData.maxId + 1];
		for (TagType type : TagType.values()) {
			BY_CLASS.put(type.getTagClass(), type);
			BY_NAME.put(type.getTypeName(), type);
			BY_ID[type.getId()] = type;
		}
	}


	private final Class<? extends Tag<?>> tagClass;
	private final String typeName;
	private final int id;

	private TagType(Class<? extends Tag<?>> tagClass, String typeName, int id) {
		this.tagClass = tagClass;
		this.typeName = typeName;
		this.id = id;
		// Such a hack, shame that Java makes this such a pain
		if (this.id > BaseData.maxId) {
			BaseData.maxId = this.id;
		}
	}

	public Class<? extends Tag<?>> getTagClass() {
		return tagClass;
	}

	public String getTypeName() {
		return typeName;
	}

	public int getId() {
		return id;
	}

	public static TagType getByTagClass(Class<? extends Tag<?>> clazz) {
		TagType ret = BY_CLASS.get(clazz);
		if (ret == null) {
			throw new IllegalArgumentException("Tag type " + clazz + " is unknown!");
		}
		return ret;
	}

	public static TagType getByTypeName(String typeName) {
		TagType ret = BY_NAME.get(typeName);
		if (ret == null) {
			throw new IllegalArgumentException("Tag type " + typeName + " is unknown!");
		}
		return ret;
	}

	public static TagType getById(int id) {
		if (id >= 0 && id < BY_ID.length) {
			TagType ret = BY_ID[id];
			if (ret == null) {
				throw new IllegalArgumentException("Tag type id " + id + " is unknown!");
			}
			return ret;
		} else {
			throw new IndexOutOfBoundsException("Tag type id " + id + " is out of bounds!");
		}
	}

	private static class BaseData {
		private static int maxId = 0;
	}
}
