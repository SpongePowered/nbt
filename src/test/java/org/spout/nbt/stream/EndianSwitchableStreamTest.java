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
package org.spout.nbt.stream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteOrder;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Test for both {@link EndianSwitchableInputStream EndianSwitchableInput} and {@link EndianSwitchableOutputStream Output} Streams
 */
public class EndianSwitchableStreamTest {
	@Test
	public void testWriteLEUnsignedShort() throws IOException {
		int unsigned = Short.MAX_VALUE + 5;
		char testChar = 'b';
		ByteArrayOutputStream rawOutput = new ByteArrayOutputStream();
		EndianSwitchableOutputStream output = new EndianSwitchableOutputStream(rawOutput, ByteOrder.LITTLE_ENDIAN);
		output.writeShort(unsigned);
		output.writeChar(testChar);

		EndianSwitchableInputStream input = new EndianSwitchableInputStream(new ByteArrayInputStream(rawOutput.toByteArray()), ByteOrder.LITTLE_ENDIAN);
		assertEquals(unsigned, input.readUnsignedShort());
		assertEquals(testChar, input.readChar());
	}
}
