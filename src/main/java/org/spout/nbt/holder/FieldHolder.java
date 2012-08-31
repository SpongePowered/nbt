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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.spout.nbt.CompoundMap;
import org.spout.nbt.CompoundTag;
import org.spout.nbt.Tag;
import org.spout.nbt.stream.NBTInputStream;
import org.spout.nbt.stream.NBTOutputStream;

/**
 * Holder class for {@link FieldValue FieldValues}
 */
public abstract class FieldHolder {
	private final List<FieldValue<?>> fields = new ArrayList<FieldValue<?>>();

	protected FieldHolder(FieldValue<?>... fields) {
		addFields(fields);
	}

	protected void addFields(FieldValue<?>... fields) {
		Collections.addAll(this.fields, fields);
	}

	public CompoundMap save() {
		CompoundMap map = new CompoundMap();
		for (FieldValue<?> field : fields) {
			field.save(map);
		}
		return map;
	}

	public void load(CompoundTag tag) {
		for (FieldValue<?> field : fields) {
			field.load(tag);
		}
	}

	public void save(File file, boolean compressed) throws IOException {
		save(new FileOutputStream(file), compressed);
	}

	public void save(OutputStream stream, boolean compressed) throws IOException {
		NBTOutputStream os = new NBTOutputStream(stream, compressed);
		os.writeTag(new CompoundTag("", save()));
	}

	public void load(File file, boolean compressed) throws IOException {
		load(new FileInputStream(file), compressed);
	}

	public void load(InputStream stream, boolean compressed) throws IOException {
		NBTInputStream is = new NBTInputStream(stream, compressed);
		Tag<?> tag = is.readTag();
		if (!(tag instanceof CompoundTag)) {
			throw new IllegalArgumentException("Expected CompoundTag, got " + tag.getClass());
		}

		CompoundTag compound = (CompoundTag) tag;
		load(compound);
	}
}
