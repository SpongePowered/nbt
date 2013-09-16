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

import java.util.ArrayList;
import java.util.List;

import org.spout.nbt.ListTag;
import org.spout.nbt.Tag;

/**
 * Represents a field that contains a list of other tags (all tags are of the same type)
 */
public class ListField<T> implements Field<List<T>> {
	private final Field<T> backingField;

	public ListField(Field<T> field) {
		this.backingField = field;
	}

	public List<T> getValue(Tag<?> tag) throws IllegalArgumentException {
		ListTag<?> listTag = FieldUtils.checkTagCast(tag, ListTag.class);
		List<T> result = new ArrayList<T>();
		for (Tag<?> element : listTag.getValue()) {
			result.add(backingField.getValue(element));
		}
		return result;
	}

	@SuppressWarnings ("unchecked")
	public Tag<?> getValue(String name, List<T> value) {
		List<Tag<?>> tags = new ArrayList<Tag<?>>();
		Class tagClazz = Tag.class; // Generics suck (I had to move this comment 3 times while finding the right place to nuke generics too)
		for (T element : value) {
			Tag<?> tag = backingField.getValue("", element);
			tagClazz = tag.getClass();
			tags.add(tag);
		}

		return new ListTag<Tag<?>>(name, tagClazz, tags);
	}
}
