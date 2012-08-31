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
package org.spout.nbt;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteOrder;

import org.spout.nbt.stream.NBTInputStream;

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
