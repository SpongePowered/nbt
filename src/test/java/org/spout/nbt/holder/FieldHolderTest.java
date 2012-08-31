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

import org.junit.Before;
import org.junit.Test;
import org.spout.nbt.CompoundMap;
import org.spout.nbt.CompoundTag;
import org.spout.nbt.StringTag;

import static org.junit.Assert.*;

/**
 * Test for {@link FieldHolder}
 */
public class FieldHolderTest {
	private ExampleHolder subject;

	@Before
	public void setUp() {
		subject = new ExampleHolder();
		CompoundMap map = new CompoundMap();
		map.put(new StringTag("name", "helloworld"));
		CompoundTag mapTag = new CompoundTag("", map);
		subject.load(mapTag);
	}

	private static class ExampleHolder extends FieldHolder {
		public final FieldValue<String> name = FieldValue.from("name", new BasicTagField<String>(StringTag.class)),
		unassignedDefault = FieldValue.from("unassigned", new BasicTagField<String>(StringTag.class), "value-here");

		public ExampleHolder() {
			addFields(name, unassignedDefault);
		}
	}

	@Test
	public void testBasicLoad() {
		assertEquals("helloworld", subject.name.get());
		assertEquals("value-here", subject.unassignedDefault.get());
	}

	@Test
	public void testDefaultValue() {
		assertEquals("value-here", subject.unassignedDefault.get());
	}
}
