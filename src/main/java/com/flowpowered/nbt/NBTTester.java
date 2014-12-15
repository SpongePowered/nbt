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
package com.flowpowered.nbt;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteOrder;

import com.flowpowered.nbt.stream.NBTInputStream;

/**
 * Entry point that accepts a file path for a NBT file.
 */
public class NBTTester {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("No files provided! Usage: <nbt file> [compressed] [byteorder]");
            System.exit(1);
        }

        final File argFile = new File(args[0]);
        final boolean compressed = args.length >= 2 ? Boolean.valueOf(args[1]) : true;
        final ByteOrder order = args.length >= 3 ? getByteOrder(args[2]) : ByteOrder.BIG_ENDIAN;

        if (!argFile.isFile()) {
            System.err.println("File " + argFile + " does not exist!");
            System.exit(1);
        }

        NBTInputStream input;
        try {
            input = new NBTInputStream(new FileInputStream(argFile), compressed, order);
        } catch (IOException e) {
            System.err.println("Error opening NBT file: " + e);
            e.printStackTrace();
            System.exit(1);
            return;
        }

        try {
            Tag tag = input.readTag();
            System.out.println("NBT data from file: " + argFile.getCanonicalPath());
            System.out.println(tag);
        } catch (IOException e) {
            System.err.println("Error reading tag from file: " + e);
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static ByteOrder getByteOrder(String name) {
        if (name.equalsIgnoreCase("big_endian") || name.equalsIgnoreCase("bigendian") || name.equalsIgnoreCase("be")) {
            return ByteOrder.BIG_ENDIAN;
        } else if (name.equalsIgnoreCase("little_endian") || name.equalsIgnoreCase("littleendian") || name.equalsIgnoreCase("le")) {
            return ByteOrder.LITTLE_ENDIAN;
        } else {
            throw new IllegalArgumentException("Unknown ByteOrder: " + name);
        }
    }
}
