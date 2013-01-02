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
package org.spout.nbt.regionfile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.InflaterInputStream;

import org.spout.nbt.Tag;
import org.spout.nbt.stream.NBTInputStream;

public class SimpleRegionFileReader {

	private static int EXPECTED_VERSION = 1;

	public static List<Tag<?>> readFile(File f) {

		RandomAccessFile raf;
		try {
			raf = new RandomAccessFile(f, "r");
		} catch (FileNotFoundException e) {
			return null;
		}

		try {
			int version = raf.readInt();

			if (version != EXPECTED_VERSION) {
				return null;
			}

			int segmentSize = raf.readInt();
			int segmentMask = (1 << segmentSize) - 1;
			int entries = raf.readInt();

			List<Tag<?>> list = new ArrayList<Tag<?>>(entries);

			int[] blockSegmentStart = new int[entries];
			int[] blockActualLength = new int[entries];

			for (int i = 0; i < entries; i++) {
				blockSegmentStart[i] = raf.readInt();
				blockActualLength[i] = raf.readInt();
			}
			
			for (int i = 0; i < entries; i++) {
				if (blockActualLength[i] == 0) {
					list.add(null);
					continue;
				}
				byte[] data = new byte[blockActualLength[i]];
				raf.seek(blockSegmentStart[i] << segmentSize);
				raf.readFully(data);
				ByteArrayInputStream in = new ByteArrayInputStream(data);
				InflaterInputStream iis = new InflaterInputStream(in);
				NBTInputStream ns = new NBTInputStream(iis, false);
				try {
					Tag<?> t = ns.readTag();
					list.add(t);
				} catch (IOException ioe) {
					list.add(null);
				}
				try {
					ns.close();
				} catch (IOException ioe) {
				}
			}
			
			return list;
		} catch (IOException ioe) {
			return null;
		} finally {
			try {
				raf.close();
			} catch (IOException ioe) {
			}
		}
	}

}
