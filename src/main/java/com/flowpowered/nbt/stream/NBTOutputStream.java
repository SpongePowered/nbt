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

import java.io.Closeable;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteOrder;
import java.util.List;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPOutputStream;

import com.flowpowered.nbt.*;

/**
 * This class writes NBT, or Named Binary Tag, {@link Tag} objects to an underlying {@link java.io.OutputStream}.
 * <p />
 * The NBT format was created by Markus Persson, and the specification may be found at <a href="https://flowpowered.com/nbt/spec.txt">
 * https://flowpowered.com/nbt/spec.txt</a>.
 */
public final class NBTOutputStream implements Closeable {
	
	private final DataOutput	dataOut;
	private final OutputStream	outputStream;

	/**
	 * Creates a new {@link NBTOutputStream}, which will write data to the specified underlying output stream. This assumes the output stream
	 * should be compressed with GZIP.
	 *
	 * @param os
	 *            The output stream.
	 * @throws java.io.IOException
	 *             if an I/O error occurs.
	 */
	public NBTOutputStream(OutputStream os) throws IOException {
		this(os, NBTInputStream.GZIP_COMPRESSION, ByteOrder.BIG_ENDIAN);
	}

	/**
	 * Creates a new {@link NBTOutputStream}, which will write data to the specified underlying output stream. A flag indicates if the output
	 * should be compressed with GZIP or not.
	 *
	 * @param os
	 *            The output stream.
	 * @param compressed
	 *            A flag that indicates if the output should be compressed.
	 * @throws java.io.IOException
	 *             if an I/O error occurs.
	 * @deprecated Use {@link #NBTOutputStream(InputStream, int)} instead
	 */
	@Deprecated
	public NBTOutputStream(OutputStream os, boolean compressed) throws IOException {
		this(os, compressed, ByteOrder.BIG_ENDIAN);
	}

	/**
	 * Creates a new {@link NBTOutputStream}, which will write data to the specified underlying output stream. The stream may be wrapped into a
	 * compressing output stream depending on the chosen compression method. A flag indicates if the output should be compressed with GZIP or
	 * not.
	 *
	 * @param os
	 *            The output stream.
	 * @param compression
	 *            The compression algorithm used for the input stream. Must be {@link NBTInputStream#NO_COMPRESSION},
	 *            {@link NBTInputStream#GZIP_COMPRESSION} or {@link NBTInputStream#ZLIB_COMPRESSION}.
	 * @throws java.io.IOException
	 *             if an I/O error occurs.
	 */
	public NBTOutputStream(OutputStream os, int compression) throws IOException {
		this(os, compression, ByteOrder.BIG_ENDIAN);
	}

	/**
	 * Creates a new {@link NBTOutputStream}, which will write data to the specified underlying output stream. A flag indicates if the output
	 * should be compressed with GZIP or not.
	 *
	 * @param os
	 *            The output stream.
	 * @param compressed
	 *            A flag that indicates if the output should be compressed.
	 * @param endianness
	 *            A flag that indicates if numbers in the output should be output in little-endian format.
	 * @throws java.io.IOException
	 *             if an I/O error occurs.
	 * @deprecated Use {@link #NBTOutputStream(InputStream, int, ByteOrder)} instead
	 */
	@Deprecated
	public NBTOutputStream(OutputStream os, boolean compressed, ByteOrder endianness) throws IOException {
		this(os, compressed ? NBTInputStream.GZIP_COMPRESSION : NBTInputStream.NO_COMPRESSION, endianness);
	}

	/**
	 * Creates a new {@link NBTOutputStream}, which will write data to the specified underlying output stream. The stream may be wrapped into a
	 * compressing output stream depending on the chosen compression method.
	 *
	 * @param os
	 *            The output stream.
	 * @param compression
	 *            The compression algorithm used for the input stream. Must be {@link NBTInputStream#NO_COMPRESSION},
	 *            {@link NBTInputStream#GZIP_COMPRESSION} or {@link NBTInputStream#ZLIB_COMPRESSION}.
	 * @param endianness
	 *            A flag that indicates if numbers in the output should be output in little-endian format.
	 * @throws java.io.IOException
	 *             if an I/O error occurs.
	 */
	public NBTOutputStream(OutputStream os, int compression, ByteOrder endianness) throws IOException {
		switch (compression) {
		case NBTInputStream.NO_COMPRESSION:
			break;
		case NBTInputStream.GZIP_COMPRESSION:
			os = new GZIPOutputStream(os);
			break;
		case NBTInputStream.ZLIB_COMPRESSION:
			os = new DeflaterOutputStream(os);
			break;
		default:
			throw new IllegalArgumentException("Unsupported compression type, must be between 0 and 2 (inclusive)");
		}
		if (endianness == ByteOrder.LITTLE_ENDIAN)
			this.outputStream = (OutputStream) (this.dataOut = new LittleEndianOutputStream(os));
		else
			this.outputStream = (OutputStream) (this.dataOut = new DataOutputStream(os));
	}

	/**
	 * Writes a tag.
	 *
	 * @param tag
	 *            The tag to write.
	 * @throws java.io.IOException
	 *             if an I/O error occurs.
	 */
	public void writeTag(Tag<?> tag) throws IOException {
		String name = tag.getName();
		byte[] nameBytes = name.getBytes(NBTConstants.CHARSET.name());

		dataOut.writeByte(tag.getType().getId());
		dataOut.writeShort(nameBytes.length);
		dataOut.write(nameBytes);

		if (tag.getType() == TagType.TAG_END) {
			throw new IOException("Named TAG_End not permitted.");
		}

		writeTagPayload(tag);
	}

	/**
	 * Writes tag payload.
	 *
	 * @param tag
	 *            The tag.
	 * @throws java.io.IOException
	 *             if an I/O error occurs.
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

		case TAG_LONG_ARRAY:
			writeLongArrayTagPayload((LongArrayTag) tag);
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
	 * @param tag
	 *            The tag.
	 * @throws java.io.IOException
	 *             if an I/O error occurs.
	 */
	private void writeByteTagPayload(ByteTag tag) throws IOException {
		dataOut.writeByte(tag.getValue());
	}

	/**
	 * Writes a {@code TAG_Byte_Array} tag.
	 *
	 * @param tag
	 *            The tag.
	 * @throws java.io.IOException
	 *             if an I/O error occurs.
	 */
	private void writeByteArrayTagPayload(ByteArrayTag tag) throws IOException {
		byte[] bytes = tag.getValue();
		dataOut.writeInt(bytes.length);
		dataOut.write(bytes);
	}

	/**
	 * Writes a {@code TAG_Compound} tag.
	 *
	 * @param tag
	 *            The tag.
	 * @throws java.io.IOException
	 *             if an I/O error occurs.
	 */
	private void writeCompoundTagPayload(CompoundTag tag) throws IOException {
		for (Tag<?> childTag : tag.getValue().values()) {
			writeTag(childTag);
		}
		dataOut.writeByte(TagType.TAG_END.getId()); // end tag - better way?
	}

	/**
	 * Writes a {@code TAG_List} tag.
	 *
	 * @param tag
	 *            The tag.
	 * @throws java.io.IOException
	 *             if an I/O error occurs.
	 */
	@SuppressWarnings("unchecked")
	private void writeListTagPayload(ListTag<?> tag) throws IOException {
		Class<? extends Tag<?>> clazz = tag.getElementType();
		List<Tag<?>> tags = (List<Tag<?>>) tag.getValue();
		int size = tags.size();

		dataOut.writeByte(TagType.getByTagClass(clazz).getId());
		dataOut.writeInt(size);
		for (Tag<?> tag1 : tags) {
			writeTagPayload(tag1);
		}
	}

	/**
	 * Writes a {@code TAG_String} tag.
	 *
	 * @param tag
	 *            The tag.
	 * @throws java.io.IOException
	 *             if an I/O error occurs.
	 */
	private void writeStringTagPayload(StringTag tag) throws IOException {
		byte[] bytes = tag.getValue().getBytes(NBTConstants.CHARSET.name());
		dataOut.writeShort(bytes.length);
		dataOut.write(bytes);
	}

	/**
	 * Writes a {@code TAG_Double} tag.
	 *
	 * @param tag
	 *            The tag.
	 * @throws java.io.IOException
	 *             if an I/O error occurs.
	 */
	private void writeDoubleTagPayload(DoubleTag tag) throws IOException {
		dataOut.writeDouble(tag.getValue());
	}

	/**
	 * Writes a {@code TAG_Float} tag.
	 *
	 * @param tag
	 *            The tag.
	 * @throws java.io.IOException
	 *             if an I/O error occurs.
	 */
	private void writeFloatTagPayload(FloatTag tag) throws IOException {
		dataOut.writeFloat(tag.getValue());
	}

	/**
	 * Writes a {@code TAG_Long} tag.
	 *
	 * @param tag
	 *            The tag.
	 * @throws java.io.IOException
	 *             if an I/O error occurs.
	 */
	private void writeLongTagPayload(LongTag tag) throws IOException {
		dataOut.writeLong(tag.getValue());
	}

	/**
	 * Writes a {@code TAG_Int} tag.
	 *
	 * @param tag
	 *            The tag.
	 * @throws java.io.IOException
	 *             if an I/O error occurs.
	 */
	private void writeIntTagPayload(IntTag tag) throws IOException {
		dataOut.writeInt(tag.getValue());
	}

	/**
	 * Writes a {@code TAG_Short} tag.
	 *
	 * @param tag
	 *            The tag.
	 * @throws java.io.IOException
	 *             if an I/O error occurs.
	 */
	private void writeShortTagPayload(ShortTag tag) throws IOException {
		dataOut.writeShort(tag.getValue());
	}

	/**
	 * Writes a {@code TAG_Int_Array} tag.
	 *
	 * @param tag
	 *            The tag.
	 * @throws java.io.IOException
	 *             if an I/O error occurs.
	 */
	private void writeIntArrayTagPayload(IntArrayTag tag) throws IOException {
		int[] ints = tag.getValue();
		dataOut.writeInt(ints.length);
		for (int i = 0; i < ints.length; i++) {
			dataOut.writeInt(ints[i]);
		}
	}

	/**
	 * Writes a {@code TAG_Long_Array} tag.
	 *
	 * @param tag
	 *            The tag.
	 * @throws java.io.IOException
	 *             if an I/O error occurs.
	 */
	private void writeLongArrayTagPayload(LongArrayTag tag) throws IOException {
		long[] longs = tag.getValue();
		dataOut.writeInt(longs.length);
		for (int i = 0; i < longs.length; i++) {
			dataOut.writeLong(longs[i]);
		}
	}

	/**
	 * Writes a {@code TAG_Short_Array} tag.
	 *
	 * @param tag
	 *            The tag.
	 * @throws java.io.IOException
	 *             if an I/O error occurs.
	 */
	private void writeShortArrayTagPayload(ShortArrayTag tag) throws IOException {
		short[] shorts = tag.getValue();
		dataOut.writeInt(shorts.length);
		for (int i = 0; i < shorts.length; i++) {
			dataOut.writeShort(shorts[i]);
		}
	}

	/**
	 * Writes a {@code TAG_Empty} tag.
	 *
	 * @param tag
	 *            The tag.
	 */
	private void writeEndTagPayload(EndTag tag) {
		/* empty */
	}

	@Override
	public void close() throws IOException {
		outputStream.close();
	}

	/**
	 * @return whether this NBTInputStream writes numbers in little-endian format.
	 */
	@Deprecated
	public ByteOrder getEndianness() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Flushes the stream
	 */
	public void flush() throws IOException {
		outputStream.flush();
	}
}
