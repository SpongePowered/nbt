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

import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteOrder;

public class LittleEndianOutputStream extends FilterOutputStream implements DataOutput {
	
	public LittleEndianOutputStream(OutputStream backingStream) {
		super(backingStream instanceof DataOutputStream ? (DataOutputStream) backingStream : new DataOutputStream(backingStream));
	}

	@Deprecated
	public ByteOrder getEndianness() {
		return ByteOrder.LITTLE_ENDIAN;
	}

	protected DataOutputStream getBackingStream() {
		return (DataOutputStream) super.out;
	}

	@Override
	public void writeBoolean(boolean b) throws IOException {
		getBackingStream().writeBoolean(b);
	}

	@Override
	public void writeByte(int i) throws IOException {
		getBackingStream().writeByte(i);
	}

	@Override
	public void writeShort(int i) throws IOException {
		getBackingStream().writeShort(Integer.reverseBytes(i) >> 16);
	}

	@Override
	public void writeChar(int i) throws IOException {
		getBackingStream().writeChar(Character.reverseBytes((char) i));
	}

	@Override
	public void writeInt(int i) throws IOException {
		getBackingStream().writeInt(Integer.reverseBytes(i));
	}

	@Override
	public void writeLong(long l) throws IOException {
		getBackingStream().writeLong(Long.reverseBytes(l));
	}

	@Override
	public void writeFloat(float v) throws IOException {
		getBackingStream().writeInt(Integer.reverseBytes(Float.floatToIntBits(v)));
	}

	@Override
	public void writeDouble(double v) throws IOException {
		getBackingStream().writeLong(Long.reverseBytes(Double.doubleToLongBits(v)));
	}

	@Override
	public void writeBytes(String s) throws IOException {
		getBackingStream().writeBytes(s);
	}

	@Override
	public void writeChars(String s) throws IOException {
		getBackingStream().writeChars(s);
	}

	@Override
	public void writeUTF(String s) throws IOException {
		getBackingStream().writeUTF(s);
	}
}
