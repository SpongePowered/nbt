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
package com.flowpowered.nbt.regionfile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.InflaterInputStream;

import com.flowpowered.nbt.Tag;
import com.flowpowered.nbt.stream.NBTInputStream;

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
