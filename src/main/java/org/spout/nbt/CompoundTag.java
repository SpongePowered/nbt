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
 * The {@code TAG_Compound} tag.
 */
public final class CompoundTag extends Tag<CompoundMap> {
	/**
	 * The value.
	 */
	private final CompoundMap value;

	/**
	 * Creates the tag.
	 * @param name The name.
	 * @param value The value.
	 */
	public CompoundTag(String name, CompoundMap value) {
		super(TagType.TAG_COMPOUND, name);
//		this.value = (CompoundMap) Collections.unmodifiableMap(value); This doesn't work anymore, needs a new solution
		this.value = value;
	}

	@Override
	public CompoundMap getValue() {
		return value;
	}

	@Override
	public String toString() {
		String name = getName();
		String append = "";
		if (name != null && !name.equals("")) {
			append = "(\"" + this.getName() + "\")";
		}

		StringBuilder bldr = new StringBuilder();
		bldr.append("TAG_Compound").append(append).append(": ").append(value.size()).append(" entries\r\n{\r\n");
		for (Tag entry : value.values()) {
			bldr.append("   ").append(entry.toString().replaceAll("\r\n", "\r\n   ")).append("\r\n");
		}
		bldr.append("}");
		return bldr.toString();
	}

	public CompoundTag clone() {
		CompoundMap map = new CompoundMap(value);
		return new CompoundTag(getName(), map);
	}
}
