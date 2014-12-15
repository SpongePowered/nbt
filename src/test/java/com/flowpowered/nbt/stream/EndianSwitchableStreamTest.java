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
