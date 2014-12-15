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
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteOrder;
import java.util.List;
import java.util.zip.GZIPOutputStream;

import com.flowpowered.nbt.ByteArrayTag;
import com.flowpowered.nbt.ByteTag;
import com.flowpowered.nbt.CompoundTag;
import com.flowpowered.nbt.DoubleTag;
import com.flowpowered.nbt.EndTag;
import com.flowpowered.nbt.FloatTag;
import com.flowpowered.nbt.IntArrayTag;
import com.flowpowered.nbt.IntTag;
import com.flowpowered.nbt.ListTag;
import com.flowpowered.nbt.LongTag;
import com.flowpowered.nbt.NBTConstants;
import com.flowpowered.nbt.ShortArrayTag;
import com.flowpowered.nbt.ShortTag;
import com.flowpowered.nbt.StringTag;
import com.flowpowered.nbt.Tag;
import com.flowpowered.nbt.TagType;

/**
 * This class writes NBT, or Named Binary Tag, {@link Tag} objects to an underlying {@link java.io.OutputStream}. <p /> The NBT format was created by Markus Persson, and the specification may be found
 * at <a href="https://flowpowered.com/nbt/spec.txt"> https://flowpowered.com/nbt/spec.txt</a>.
 */
public final class NBTOutputStream implements Closeable {
    /**
     * The output stream.
     */
    private final EndianSwitchableOutputStream os;

    /**
     * Creates a new {@link NBTOutputStream}, which will write data to the specified underlying output stream. This assumes the output stream should be compressed with GZIP.
     *
     * @param os The output stream.
     * @throws java.io.IOException if an I/O error occurs.
     */
    public NBTOutputStream(OutputStream os) throws IOException {
        this(os, true, ByteOrder.BIG_ENDIAN);
    }

    /**
     * Creates a new {@link NBTOutputStream}, which will write data to the specified underlying output stream. A flag indicates if the output should be compressed with GZIP or not.
     *
     * @param os The output stream.
     * @param compressed A flag that indicates if the output should be compressed.
     * @throws java.io.IOException if an I/O error occurs.
     */
    public NBTOutputStream(OutputStream os, boolean compressed) throws IOException {
        this(os, compressed, ByteOrder.BIG_ENDIAN);
    }

    /**
     * Creates a new {@link NBTOutputStream}, which will write data to the specified underlying output stream. A flag indicates if the output should be compressed with GZIP or not.
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
    @SuppressWarnings ("unchecked")
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

    /**
     * Flushes the stream
     */
    public void flush() throws IOException {
        os.flush();
    }
}
