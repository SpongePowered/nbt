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

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteOrder;

/**
 * A wrapper around {@link DataInputStream} that allows changing the endianness of data.
 * By default, everything in Java is big-endian
 */
public class EndianSwitchableInputStream extends FilterInputStream implements DataInput {
	private final ByteOrder endianness;

	public EndianSwitchableInputStream(InputStream stream, ByteOrder endianness) {
		super(stream instanceof DataInputStream ? stream : new DataInputStream(stream));
		this.endianness = endianness;
	}

	public ByteOrder getEndianness() {
		return endianness;
	}

	protected DataInputStream getBackingStream() {
		return (DataInputStream) super.in;
	}

	public void readFully(byte[] bytes) throws IOException {
		getBackingStream().readFully(bytes);
	}

	public void readFully(byte[] bytes, int i, int i1) throws IOException {
		getBackingStream().readFully(bytes, i, i1);
	}

	public int skipBytes(int i) throws IOException {
		return getBackingStream().skipBytes(i);
	}

	public boolean readBoolean() throws IOException {
		return getBackingStream().readBoolean();
	}

	public byte readByte() throws IOException {
		return getBackingStream().readByte();
	}

	public int readUnsignedByte() throws IOException {
		return getBackingStream().readUnsignedByte();
	}

	public short readShort() throws IOException {
		short ret = getBackingStream().readShort();
		if (endianness == ByteOrder.LITTLE_ENDIAN) {
			ret = Short.reverseBytes(ret);
		}
		return ret;
	}

	public int readUnsignedShort() throws IOException {
		int ret = getBackingStream().readUnsignedShort();
		if (endianness == ByteOrder.LITTLE_ENDIAN) {
			ret = (char) (Integer.reverseBytes(ret) >> 16);
		}
		return ret;
	}

	public char readChar() throws IOException {
		char ret = getBackingStream().readChar();
		if (endianness == ByteOrder.LITTLE_ENDIAN) {
			ret = Character.reverseBytes(ret);
		}
		return ret;
	}

	public int readInt() throws IOException {
		return endianness == ByteOrder.LITTLE_ENDIAN ? Integer.reverseBytes(getBackingStream().readInt()) : getBackingStream().readInt();
	}

	public long readLong() throws IOException {
		return endianness == ByteOrder.LITTLE_ENDIAN ? Long.reverseBytes(getBackingStream().readLong()) : getBackingStream().readLong();
	}

	public float readFloat() throws IOException {
		int result = readInt();
		if (endianness == ByteOrder.LITTLE_ENDIAN) {
			result = Integer.reverseBytes(result);
		}
		return Float.intBitsToFloat(result);
	}

	public double readDouble() throws IOException {
		long result = readLong();
		if (endianness == ByteOrder.LITTLE_ENDIAN) {
			result = Long.reverseBytes(result);
		}
		return Double.longBitsToDouble(result);
	}

	@SuppressWarnings("deprecation") // This method is deprecated
	public String readLine() throws IOException {
		return getBackingStream().readLine();
	}

	public String readUTF() throws IOException {
		return getBackingStream().readUTF();
	}
}
