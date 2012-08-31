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
package org.spout.nbt.stream;

import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteOrder;

/**
 * @author zml2008
 */
public class EndianSwitchableOutputStream extends FilterOutputStream implements DataOutput {
	private final ByteOrder endianness;

	public EndianSwitchableOutputStream(OutputStream backingStream, ByteOrder endianness) {
		super(backingStream instanceof DataOutputStream ? (DataOutputStream) backingStream : new DataOutputStream(backingStream));
		this.endianness = endianness;
	}

	public ByteOrder getEndianness() {
		return endianness;
	}

	protected DataOutputStream getBackingStream() {
		return (DataOutputStream) super.out;
	}

	public void writeBoolean(boolean b) throws IOException {
		getBackingStream().writeBoolean(b);
	}

	public void writeByte(int i) throws IOException {
		getBackingStream().writeByte(i);
	}

	public void writeShort(int i) throws IOException {
		if (endianness == ByteOrder.LITTLE_ENDIAN) {
			i = Integer.reverseBytes(i) >> 16;
		}
		getBackingStream().writeShort(i);
	}

	public void writeChar(int i) throws IOException {
		if (endianness == ByteOrder.LITTLE_ENDIAN) {
			i = Character.reverseBytes((char) i);
		}
		getBackingStream().writeChar(i);
	}

	public void writeInt(int i) throws IOException {
		if (endianness == ByteOrder.LITTLE_ENDIAN) {
			i = Integer.reverseBytes(i);
		}
		getBackingStream().writeInt(i);
	}

	public void writeLong(long l) throws IOException {
		if (endianness == ByteOrder.LITTLE_ENDIAN) {
			l = Long.reverseBytes(l);
		}
		getBackingStream().writeLong(l);
	}

	public void writeFloat(float v) throws IOException {
		int intBits = Float.floatToIntBits(v);
		if (endianness == ByteOrder.LITTLE_ENDIAN) {
			intBits = Integer.reverseBytes(intBits);
		}
		getBackingStream().writeInt(intBits);
	}

	public void writeDouble(double v) throws IOException {
		long longBits = Double.doubleToLongBits(v);
		if (endianness == ByteOrder.LITTLE_ENDIAN) {
			longBits = Long.reverseBytes(longBits);
		}
		getBackingStream().writeLong(longBits);
	}

	public void writeBytes(String s) throws IOException {
		getBackingStream().writeBytes(s);
	}

	public void writeChars(String s) throws IOException {
		getBackingStream().writeChars(s);
	}

	public void writeUTF(String s) throws IOException {
		getBackingStream().writeUTF(s);
	}
}
