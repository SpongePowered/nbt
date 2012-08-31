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
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteOrder;
import java.util.List;
import java.util.zip.GZIPOutputStream;

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
import org.spout.nbt.ShortArrayTag;
import org.spout.nbt.ShortTag;
import org.spout.nbt.StringTag;
import org.spout.nbt.Tag;
import org.spout.nbt.TagType;

/**
 * This class writes NBT, or Named Binary Tag, {@link Tag} objects to an
 * underlying {@link java.io.OutputStream}.
 * <p />
 * The NBT format was created by Markus Persson, and the specification may be
 * found at <a href="http://www.minecraft.net/docs/NBT.txt">
 * http://www.minecraft.net/docs/NBT.txt</a>.
 */
public final class NBTOutputStream implements Closeable {
	/**
	 * The output stream.
	 */
	private final EndianSwitchableOutputStream os;

	/**
	 * Creates a new {@link NBTOutputStream}, which will write data to the
	 * specified underlying output stream. This assumes the output stream should
	 * be compressed with GZIP.
	 *
	 * @param os The output stream.
	 * @throws java.io.IOException if an I/O error occurs.
	 */
	public NBTOutputStream(OutputStream os) throws IOException {
		this(os, true, ByteOrder.BIG_ENDIAN);
	}

	/**
	 * Creates a new {@link NBTOutputStream}, which will write data to the
	 * specified underlying output stream. A flag indicates if the output should
	 * be compressed with GZIP or not.
	 *
	 * @param os The output stream.
	 * @param compressed A flag that indicates if the output should be compressed.
	 * @throws java.io.IOException if an I/O error occurs.
	 */
	public NBTOutputStream(OutputStream os, boolean compressed) throws IOException {
		this(os, compressed, ByteOrder.BIG_ENDIAN);
	}

	/**
	 * Creates a new {@link NBTOutputStream}, which will write data to the
	 * specified underlying output stream. A flag indicates if the output should
	 * be compressed with GZIP or not.
	 *
	 * @param os The output stream.
	 * @param compressed A flag that indicates if the output should be compressed.
	 * @param endianness A flag that indicates if numbers in the output should be output in little-endian format.
	 * @throws java.io.IOException if an I/O error occurs.
	 */
	public NBTOutputStream(OutputStream os, boolean compressed, ByteOrder endianness) throws IOException {
		this.os = new EndianSwitchableOutputStream(compressed ? new GZIPOutputStream(os) : os, endianness);
	}

	/**
	 * Writes a tag.
	 *
	 * @param tag The tag to write.
	 * @throws java.io.IOException if an I/O error occurs.
	 */
	public void writeTag(Tag<?> tag) throws IOException {
		String name = tag.getName();
		byte[] nameBytes = name.getBytes(NBTConstants.CHARSET.name());

		os.writeByte(tag.getType().getId());
		os.writeShort(nameBytes.length);
		os.write(nameBytes);

		if (tag.getType() == TagType.TAG_END) {
			throw new IOException("Named TAG_End not permitted.");
		}

		writeTagPayload(tag);
	}

	/**
	 * Writes tag payload.
	 *
	 * @param tag The tag.
	 * @throws java.io.IOException if an I/O error occurs.
	 */
	private void writeTagPayload(Tag<?> tag) throws IOException {
		switch (tag.getType()) {
			case TAG_END:
				writeEndTagPayload((EndTag) tag);
				break;

			case TAG_BYTE:
				writeByteTagPayload((ByteTag) tag);
				break;

			case TAG_SHORT:
				writeShortTagPayload((ShortTag) tag);
				break;

			case TAG_INT:
				writeIntTagPayload((IntTag) tag);
				break;

			case TAG_LONG:
				writeLongTagPayload((LongTag) tag);
				break;

			case TAG_FLOAT:
				writeFloatTagPayload((FloatTag) tag);
				break;

			case TAG_DOUBLE:
				writeDoubleTagPayload((DoubleTag) tag);
				break;

			case TAG_BYTE_ARRAY:
				writeByteArrayTagPayload((ByteArrayTag) tag);
				break;

			case TAG_STRING:
				writeStringTagPayload((StringTag) tag);
				break;

			case TAG_LIST:
				writeListTagPayload((ListTag<?>) tag);
				break;

			case TAG_COMPOUND:
				writeCompoundTagPayload((CompoundTag) tag);
				break;

			case TAG_INT_ARRAY:
				writeIntArrayTagPayload((IntArrayTag) tag);
				break;

			case TAG_SHORT_ARRAY:
				writeShortArrayTagPayload((ShortArrayTag) tag);
				break;

			default:
				throw new IOException("Invalid tag type: " + tag.getType() + ".");
		}
	}

	/**
	 * Writes a {@code TAG_Byte} tag.
	 *
	 * @param tag The tag.
	 * @throws java.io.IOException if an I/O error occurs.
	 */
	private void writeByteTagPayload(ByteTag tag) throws IOException {
		os.writeByte(tag.getValue());
	}

	/**
	 * Writes a {@code TAG_Byte_Array} tag.
	 *
	 * @param tag The tag.
	 * @throws java.io.IOException if an I/O error occurs.
	 */
	private void writeByteArrayTagPayload(ByteArrayTag tag) throws IOException {
		byte[] bytes = tag.getValue();
		os.writeInt(bytes.length);
		os.write(bytes);
	}

	/**
	 * Writes a {@code TAG_Compound} tag.
	 *
	 * @param tag The tag.
	 * @throws java.io.IOException if an I/O error occurs.
	 */
	private void writeCompoundTagPayload(CompoundTag tag) throws IOException {
		for (Tag<?> childTag : tag.getValue().values()) {
			writeTag(childTag);
		}
		os.writeByte(TagType.TAG_END.getId()); // end tag - better way?
	}

	/**
	 * Writes a {@code TAG_List} tag.
	 *
	 * @param tag The tag.
	 * @throws java.io.IOException if an I/O error occurs.
	 */
	@SuppressWarnings("unchecked")
	private void writeListTagPayload(ListTag<?> tag) throws IOException {
		Class<? extends Tag<?>> clazz = tag.getElementType();
		List<Tag<?>> tags = (List<Tag<?>>) tag.getValue();
		int size = tags.size();

		os.writeByte(TagType.getByTagClass(clazz).getId());
		os.writeInt(size);
		for (Tag<?> tag1 : tags) {
			writeTagPayload(tag1);
		}
	}

	/**
	 * Writes a {@code TAG_String} tag.
	 *
	 * @param tag The tag.
	 * @throws java.io.IOException if an I/O error occurs.
	 */
	private void writeStringTagPayload(StringTag tag) throws IOException {
		byte[] bytes = tag.getValue().getBytes(NBTConstants.CHARSET.name());
		os.writeShort(bytes.length);
		os.write(bytes);
	}

	/**
	 * Writes a {@code TAG_Double} tag.
	 *
	 * @param tag The tag.
	 * @throws java.io.IOException if an I/O error occurs.
	 */
	private void writeDoubleTagPayload(DoubleTag tag) throws IOException {
		os.writeDouble(tag.getValue());
	}

	/**
	 * Writes a {@code TAG_Float} tag.
	 *
	 * @param tag The tag.
	 * @throws java.io.IOException if an I/O error occurs.
	 */
	private void writeFloatTagPayload(FloatTag tag) throws IOException {
		os.writeFloat(tag.getValue());
	}

	/**
	 * Writes a {@code TAG_Long} tag.
	 *
	 * @param tag The tag.
	 * @throws java.io.IOException if an I/O error occurs.
	 */
	private void writeLongTagPayload(LongTag tag) throws IOException {
		os.writeLong(tag.getValue());
	}

	/**
	 * Writes a {@code TAG_Int} tag.
	 *
	 * @param tag The tag.
	 * @throws java.io.IOException if an I/O error occurs.
	 */
	private void writeIntTagPayload(IntTag tag) throws IOException {
		os.writeInt(tag.getValue());
	}

	/**
	 * Writes a {@code TAG_Short} tag.
	 *
	 * @param tag The tag.
	 * @throws java.io.IOException if an I/O error occurs.
	 */
	private void writeShortTagPayload(ShortTag tag) throws IOException {
		os.writeShort(tag.getValue());
	}

	/**
	 * Writes a {@code TAG_Int_Array} tag.
	 *
	 * @param tag The tag.
	 * @throws java.io.IOException if an I/O error occurs.
	 */
	private void writeIntArrayTagPayload(IntArrayTag tag) throws IOException {
		int[] ints = tag.getValue();
		os.writeInt(ints.length);
		for (int i = 0; i < ints.length; i++) {
			os.writeInt(ints[i]);
		}
	}

	/**
	 * Writes a {@code TAG_Short_Array} tag.
	 *
	 * @param tag The tag.
	 * @throws java.io.IOException if an I/O error occurs.
	 */
	private void writeShortArrayTagPayload(ShortArrayTag tag) throws IOException {
		short[] shorts = tag.getValue();
		os.writeInt(shorts.length);
		for (int i = 0; i < shorts.length; i++) {
			os.writeShort(shorts[i]);
		}
	}

	/**
	 * Writes a {@code TAG_Empty} tag.
	 *
	 * @param tag The tag.
	 */
	private void writeEndTagPayload(EndTag tag) {
		/* empty */
	}

	public void close() throws IOException {
		os.close();
	}

	/**
	 * @return whether this NBTInputStream writes numbers in little-endian format.
	 */
	public ByteOrder getEndianness() {
		return os.getEndianness();
	}
}
