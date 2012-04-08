/*
 * This file is part of SpoutNBT (http://www.spout.org/).
 *
 * SpoutNBT is licensed under the SpoutDev License Version 1.
 *
 * SpoutNBT is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the SpoutDev License Version 1.
 *
 * SpoutNBT is distributed in the hope that it will be useful,
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

import java.io.Closeable;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import org.spout.nbt.ByteArrayTag;
import org.spout.nbt.ByteTag;
import org.spout.nbt.CompoundTag;
import org.spout.nbt.DoubleTag;
import org.spout.nbt.EndTag;
import org.spout.nbt.FloatTag;
import org.spout.nbt.IntArrayTag;
import org.spout.nbt.IntTag;
import org.spout.nbt.ListTag;
import org.spout.nbt.LongTag;
import org.spout.nbt.NBTConstants;
import org.spout.nbt.NBTUtils;
import org.spout.nbt.ShortArrayTag;
import org.spout.nbt.ShortTag;
import org.spout.nbt.StringTag;
import org.spout.nbt.Tag;

/**
 * This class reads NBT, or Named Binary Tag streams, and produces an object
 * graph of subclasses of the {@link Tag} object.
 * <p />
 * The NBT format was created by Markus Persson, and the specification may
 * be found at <a href="http://www.minecraft.net/docs/NBT.txt">
 * http://www.minecraft.net/docs/NBT.txt</a>.
 * @author Graham Edgecombe
 */
public final class NBTInputStream implements Closeable {
	/**
	 * The data input stream.
	 */
	private final DataInputStream is;

	/**
	 * Controls whether this NBTInputStream reads numbers in little-endian format.
	 */
	private final boolean littleEndian;

	/**
	 * Creates a new {@link NBTInputStream}, which will source its data
	 * from the specified input stream. This assumes the stream is compressed.
	 * @param is The input stream.
	 * @throws java.io.IOException if an I/O error occurs.
	 */
	public NBTInputStream(InputStream is) throws IOException {
		this(is, true, false);
	}

	/**
	 * Creates a new {@link NBTInputStream}, which sources its data from the
	 * specified input stream. A flag must be passed which indicates if the
	 * stream is compressed with GZIP or not. This assumes the stream
	 * uses big endian encoding.
	 * @param is The input stream.
	 * @param compressed A flag indicating if the stream is compressed.
	 * @throws java.io.IOException if an I/O error occurs.
	 */
	public NBTInputStream(InputStream is, boolean compressed) throws IOException {
		this(is, compressed, false);
	}

	/**
	 * Creates a new {@link NBTInputStream}, which sources its data from the
	 * specified input stream. A flag must be passed which indicates if the
	 * stream is compressed with GZIP or not.
	 * @param is The input stream.
	 * @param compressed A flag indicating if the stream is compressed.
	 * @param littleEndian Whether to read numbers from the InputStream with little endian encoding.
	 * @throws java.io.IOException if an I/O error occurs.
	 */
	public NBTInputStream(InputStream is, boolean compressed, boolean littleEndian) throws IOException {
		this.littleEndian = littleEndian;
		this.is = new DataInputStream(compressed ? new GZIPInputStream(is) : is);
	}

	/**
	 * Reads an NBT {@link Tag} from the stream.
	 * @return The tag that was read.
	 * @throws java.io.IOException if an I/O error occurs.
	 */
	public Tag readTag() throws IOException {
		return readTag(0);
	}

	/**
	 * Reads an NBT {@link Tag} from the stream.
	 * @param depth The depth of this tag.
	 * @return The tag that was read.
	 * @throws java.io.IOException if an I/O error occurs.
	 */
	private Tag readTag(int depth) throws IOException {
		int type = is.readByte() & 0xFF;

		String name;
		if (type != NBTConstants.TYPE_END) {
			int nameLength = is.readShort() & 0xFFFF;
			if (littleEndian) nameLength = Short.reverseBytes((short) nameLength);
			byte[] nameBytes = new byte[nameLength];
			is.readFully(nameBytes);
			name = new String(nameBytes, NBTConstants.CHARSET.name());
		} else {
			name = "";
		}

		return readTagPayload(type, name, depth);
	}

	/**
	 * Reads the payload of a {@link Tag}, given the name and type.
	 * @param type The type.
	 * @param name The name.
	 * @param depth The depth.
	 * @return The tag.
	 * @throws java.io.IOException if an I/O error occurs.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Tag readTagPayload(int type, String name, int depth) throws IOException {
		switch (type) {
		case NBTConstants.TYPE_END:
			if (depth == 0) {
				throw new IOException("TAG_End found without a TAG_Compound/TAG_List tag preceding it.");
			} else {
				return new EndTag();
			}

		case NBTConstants.TYPE_BYTE:
			return new ByteTag(name, is.readByte());

		case NBTConstants.TYPE_SHORT:
			return new ShortTag(name, (littleEndian? Short.reverseBytes(is.readShort()) : is.readShort()));

		case NBTConstants.TYPE_INT:
			return new IntTag(name, (littleEndian? Integer.reverseBytes(is.readInt()) : is.readInt()));

		case NBTConstants.TYPE_LONG:
			return new LongTag(name, (littleEndian? Long.reverseBytes(is.readLong()) : is.readLong()));

		case NBTConstants.TYPE_FLOAT:
			return new FloatTag(name, (littleEndian? Float.intBitsToFloat(Integer.reverseBytes(is.readInt())) : 
				is.readFloat()));

		case NBTConstants.TYPE_DOUBLE:
			return new DoubleTag(name, (littleEndian? Double.longBitsToDouble(Long.reverseBytes(is.readLong())) :
				is.readDouble()));

		case NBTConstants.TYPE_BYTE_ARRAY:
			int length = (littleEndian? Integer.reverseBytes(is.readInt()) : is.readInt());
			byte[] bytes = new byte[length];
			is.readFully(bytes);
			return new ByteArrayTag(name, bytes);

		case NBTConstants.TYPE_STRING:
			length = (littleEndian? Short.reverseBytes(is.readShort()) : is.readShort());
			bytes = new byte[length];
			is.readFully(bytes);
			return new StringTag(name, new String(bytes, NBTConstants.CHARSET.name()));

		case NBTConstants.TYPE_LIST:
			int childType = is.readByte();
			length = (littleEndian? Integer.reverseBytes(is.readInt()) : is.readInt());

			Class<? extends Tag> clazz = NBTUtils.getTypeClass(childType);
			List<Tag> tagList = new ArrayList<Tag>();
			for (int i = 0; i < length; i++) {
				Tag tag = readTagPayload(childType, "", depth + 1);
				if (tag instanceof EndTag) {
					throw new IOException("TAG_End not permitted in a list.");
				} else if (!clazz.isInstance(tag)) {
					throw new IOException("Mixed tag types within a list.");
				}
				tagList.add(tag);
			}

			return new ListTag(name, clazz, tagList);

		case NBTConstants.TYPE_COMPOUND:
			List<Tag> compoundTagList = new ArrayList<Tag>();
			while (true) {
				Tag tag = readTag(depth + 1);
				if(tag instanceof EndTag) {
					break;
				} else {
					compoundTagList.add(tag);
				}
			}

			return new CompoundTag(name, compoundTagList);
			
		case NBTConstants.TYPE_INT_ARRAY:
			length = (littleEndian? Integer.reverseBytes(is.readInt()) : is.readInt());
			int[] ints = new int[length];
			for(int i = 0; i < length; i++) {
				ints[i] = (littleEndian? Integer.reverseBytes(is.readInt()) : is.readInt());
			}
			return new IntArrayTag(name, ints);
			
		case NBTConstants.TYPE_SHORT_ARRAY:
			length = (littleEndian? Integer.reverseBytes(is.readInt()) : is.readInt());
			short[] shorts = new short[length];
			for(int i = 0; i < length; i++) {
				shorts[i] = (littleEndian? Short.reverseBytes(is.readShort()) : is.readShort());
			}
			return new ShortArrayTag(name, shorts);

		default:
			throw new IOException("Invalid tag type: " + type + ".");
		}
	}

	public void close() throws IOException {
		is.close();
	}

	/**
	 * @return whether this NBTInputStream reads numbers in little-endian format.
	 */
	public boolean isLittleEndian() {
		return littleEndian;
	}
}
