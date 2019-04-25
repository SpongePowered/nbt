/*
 * This file is part of Flow NBT, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2011 Flow Powered <https://flowpowered.com/>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.flowpowered.nbt.stream;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteOrder;

/**
 * A wrapper around {@link DataInputStream} that allows changing the endianness of data. By default, everything in Java is big-endian
 */
public class LittleEndianInputStream extends FilterInputStream implements DataInput {

	public LittleEndianInputStream(InputStream stream) {
		super(stream instanceof DataInputStream ? stream : new DataInputStream(stream));
	}

	@Deprecated
	public ByteOrder getEndianness() {
		return ByteOrder.LITTLE_ENDIAN;
	}

	protected DataInputStream getBackingStream() {
		return (DataInputStream) super.in;
	}

	@Override
	public void readFully(byte[] bytes) throws IOException {
		getBackingStream().readFully(bytes);
	}

	@Override
	public void readFully(byte[] bytes, int i, int i1) throws IOException {
		getBackingStream().readFully(bytes, i, i1);
	}

	@Override
	public int skipBytes(int i) throws IOException {
		return getBackingStream().skipBytes(i);
	}

	@Override
	public boolean readBoolean() throws IOException {
		return getBackingStream().readBoolean();
	}

	@Override
	public byte readByte() throws IOException {
		return getBackingStream().readByte();
	}

	@Override
	public int readUnsignedByte() throws IOException {
		return getBackingStream().readUnsignedByte();
	}

	@Override
	public short readShort() throws IOException {
		return Short.reverseBytes(getBackingStream().readShort());
	}

	@Override
	public int readUnsignedShort() throws IOException {
		return (char) (Integer.reverseBytes(getBackingStream().readUnsignedShort()) >> 16);
	}

	@Override
	public char readChar() throws IOException {
		return Character.reverseBytes(getBackingStream().readChar());
	}

	@Override
	public int readInt() throws IOException {
		return Integer.reverseBytes(getBackingStream().readInt());
	}

	@Override
	public long readLong() throws IOException {
		return Long.reverseBytes(getBackingStream().readLong());
	}

	@Override
	public float readFloat() throws IOException {
		return Float.intBitsToFloat(Integer.reverseBytes(readInt()));
	}

	@Override
	public double readDouble() throws IOException {
		return Double.longBitsToDouble(Long.reverseBytes(readLong()));
	}

	@Override
	@SuppressWarnings("deprecation") // This method is deprecated
	public String readLine() throws IOException {
		return getBackingStream().readLine();
	}

	@Override
	public String readUTF() throws IOException {
		return getBackingStream().readUTF();
	}
}
