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

    @SuppressWarnings ("deprecation") // This method is deprecated
    public String readLine() throws IOException {
        return getBackingStream().readLine();
    }

    public String readUTF() throws IOException {
        return getBackingStream().readUTF();
    }
}
