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
package org.spout.nbt.stream;

import java.io.Closeable;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.spout.nbt.ByteArrayTag;
import org.spout.nbt.ByteTag;
import org.spout.nbt.CompoundMap;
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
import org.spout.nbt.TagType;

/**
 * This class reads NBT, or Named Binary Tag streams, and produces an object
 * graph of subclasses of the {@link Tag} object.
 * <p />
 * The NBT format was created by Markus Persson, and the specification may
 * be found at <a href="http://www.minecraft.net/docs/NBT.txt">
 * http://www.minecraft.net/docs/NBT.txt</a>.
 */
public final class NBTInputStream implements Closeable {
	/**
	 * The data input stream.
	 */
	private final EndianSwitchableInputStream is;

	/**
	 * Creates a new {@link NBTInputStream}, which will source its data
	 * from the specified input stream. This assumes the stream is compressed.
	 * @param is The input stream.
	 * @throws java.io.IOException if an I/O error occurs.
	 */
	public NBTInputStream(InputStream is) throws IOException {
		this(is, true, ByteOrder.BIG_ENDIAN);
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
		this(is, compressed, ByteOrder.BIG_ENDIAN);
	}

	/**
	 * Creates a new {@link NBTInputStream}, which sources its data from the
	 * specified input stream. A flag must be passed which indicates if the
	 * stream is compressed with GZIP or not.
	 * @param is The input stream.
	 * @param compressed A flag indicating if the stream is compressed.
	 * @param endianness Whether to read numbers from the InputStream with little endian encoding.
	 * @throws java.io.IOException if an I/O error occurs.
	 */
	public NBTInputStream(InputStream is, boolean compressed, ByteOrder endianness) throws IOException {
		this.is = new EndianSwitchableInputStream(compressed ? new GZIPInputStream(is) : is, endianness);
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
		int typeId = is.readByte() & 0xFF;
		TagType type = TagType.getById(typeId);

		String name;
		if (type != TagType.TAG_END) {
			int nameLength = is.readShort() & 0xFFFF;
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
	private Tag readTagPayload(TagType type, String name, int depth) throws IOException {
		switch (type) {
			case TAG_END:
				if (depth == 0) {
					throw new IOException("TAG_End found without a TAG_Compound/TAG_List tag preceding it.");
				} else {
					return new EndTag();
				}

			case TAG_BYTE:
				return new ByteTag(name, is.readByte());

			case TAG_SHORT:
				return new ShortTag(name, is.readShort());

			case TAG_INT:
				return new IntTag(name, is.readInt());

			case TAG_LONG:
				return new LongTag(name, is.readLong());

			case TAG_FLOAT:
				return new FloatTag(name, is.readFloat());

			case TAG_DOUBLE:
				return new DoubleTag(name, is.readDouble());

			case TAG_BYTE_ARRAY:
				int length = is.readInt();
				byte[] bytes = new byte[length];
				is.readFully(bytes);
				return new ByteArrayTag(name, bytes);

			case TAG_STRING:
				length = is.readShort();
				bytes = new byte[length];
				is.readFully(bytes);
				return new StringTag(name, new String(bytes, NBTConstants.CHARSET.name()));

			case TAG_LIST:
				TagType childType = TagType.getById(is.readByte());
				length = is.readInt();

				Class<? extends Tag> clazz = childType.getTagClass();
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

			case TAG_COMPOUND:
				CompoundMap compoundTagList = new CompoundMap();
				while (true) {
					Tag tag = readTag(depth + 1);
					if (tag instanceof EndTag) {
						break;
					} else {
						compoundTagList.put(tag);
					}
				}

				return new CompoundTag(name, compoundTagList);

			case TAG_INT_ARRAY:
				length = is.readInt();
				int[] ints = new int[length];
				for (int i = 0; i < length; i++) {
					ints[i] = is.readInt();
				}
				return new IntArrayTag(name, ints);

			case TAG_SHORT_ARRAY:
				length = is.readInt();
				short[] shorts = new short[length];
				for (int i = 0; i < length; i++) {
					shorts[i] = is.readShort();
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
	public ByteOrder getByteOrder() {
		return is.getEndianness();
	}
}
