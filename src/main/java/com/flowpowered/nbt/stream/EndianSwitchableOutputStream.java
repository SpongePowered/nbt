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
