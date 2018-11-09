package com.flowpowered.nbt.regionfile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Objects;

import com.flowpowered.nbt.CompoundTag;
import com.flowpowered.nbt.Tag;
import com.flowpowered.nbt.stream.NBTInputStream;
import com.flowpowered.nbt.stream.NBTOutputStream;

/**
 * Each instance of this class represents a Minecraft chunk with a timestamp. The data is represented as a binary blob in form of a
 * {@link ByteBuffer}. Each object is self-contained and not linked to any physical file or other object. Each object is immutable and
 * should be treated as such. The data is set in the constructor</br>
 * The data must have a length of a multiple of 4096 bytes (to be able to write it to disk more easily). The four bytes specify the amount
 * of actual data bytes in the buffer following. The fifth byte contains the compression method. All following bytes are NBT data.
 * 
 * @author piegames
 */
public class Chunk {
	protected final ByteBuffer	data;
	/**
	 * The point in time when this chunk last got written. Equal to {@code (int) (System.currentTimeMillis() / 1000L)}
	 */
	public final int			timestamp;

	public Chunk(int timestamp, ByteBuffer data) {
		this.timestamp = timestamp;
		if ((data.capacity() & 4095) != 0)
			throw new IllegalArgumentException("Data buffer size must be multiple of 4096, but is " + data.capacity());
		this.data = Objects.requireNonNull(data);
	}

	/**
	 * Create a Chunk object by reading specified data from a region file (*.mca, *.mcr).
	 * 
	 * @param raf
	 *            The file channel to the region file from which to load the data
	 * @param start
	 *            The number of the 4096 byte sector where the chunk is located in the file. Don't forget that the first five bytes are used to
	 *            store the size and compression of the chunk. The position of the first byte of NBT data is thus {@code start*4096 + 5}.
	 * @param length
	 *            The amount of 4096 byte sectors to load. It should be large enough to contain all NBT data in that chunk or it will be
	 *            corrupted.
	 */
	public Chunk(int timestamp, FileChannel raf, int start, int length) throws IOException {
		this.timestamp = timestamp;
		data = ByteBuffer.allocate(4096 * length);
		raf.read(data, 4096 * start);
		data.flip();
	}

	/**
	 * Create a chunk by filling the NBT tag's data to a {@link ByteBuffer} using the specified compression method.
	 * 
	 * @param data
	 *            The NBT data the chunk will contain. Should be a {@link CompoundTag}
	 * @param compression
	 *            The compression to use.
	 */
	public Chunk(int timestamp, Tag<?> data, byte compression) throws IOException {
		this.timestamp = timestamp;
		try (ByteArrayOutputStream baos = new ByteArrayOutputStream(4096);
				NBTOutputStream out = new NBTOutputStream(baos)) {
			out.writeTag(data);
			out.flush();
			out.close();

			byte[] bytes = baos.toByteArray();
			int sectionLength = (bytes.length + 5) / 4096 + 1;
			this.data = ByteBuffer.allocate(sectionLength * 4096);
			this.data.putInt(bytes.length + 1);
			this.data.put(compression);
			this.data.put(bytes);
			this.data.flip();
		}
	}

	/** Create a new chunk with same content, but different timestamp than the previous */
	public Chunk(int timestamp, Chunk data) {
		this.timestamp = timestamp;
		this.data = data.data;
	}

	/**
	 * Returns the compression method used in this chunk as specified by the format. This value corresponds to the compression that an
	 * {@link NBTInputStream} takes in its constructor.
	 */
	public byte getCompression() {
		return data.get(4);
	}

	/**
	 * The real length of the NBT data in this chunk in bytes.
	 */
	public int getRealLength() {
		return data.getInt(0) - 1;
	}

	/**
	 * Get the amount of 4kiB-sized sectors on the hard disk that would be required to save this chunk.
	 */
	public int getSectorLength() {
		return (getRealLength() + 5) / 4096 + 1;
	}

	/**
	 * Returns the {@link ByteBuffer} containing all the data in this chunk, including the five bytes before the actual NBT data. It will always
	 * contain a multiple of 4096 bytes. Altering its content will result in undefined behavior!
	 */
	public ByteBuffer getData() {
		return data;
	}

	/**
	 * Open an {@link NBTInputStream} for reading the NBT data contained in that chunk.
	 */
	public NBTInputStream getInputStream() throws IOException {
		return new NBTInputStream(new ByteArrayInputStream(data.array(), 5, getRealLength()), getCompression());
	}

	/**
	 * Reads the NBT chunk data and returns it. The normally nameless root tag will be renamed to "chunk".
	 */
	public CompoundTag readTag() throws IOException {
		CompoundTag tag = null;
		try (NBTInputStream nbtIn = getInputStream();) {
			tag = new CompoundTag("chunk", ((CompoundTag) nbtIn.readTag()).getValue());
		}
		return tag;
	}

	/**
	 * Return a timestamp in the format used by Minecraft representing the point in time this method was called
	 * 
	 * @see #timestamp
	 */
	public static int getCurrentTimestamp() {
		return (int) (System.currentTimeMillis() / 1000L);
	}

	public static int bitsPerIndex(long[] blocks) {
		/* There are {@code 16*16*16=4096} blocks in each chunk, and a long has 64 bits */
		return blocks.length * 64 / 4096;
	}

	/**
	 * Extract a palette index from the long array. This data is located at {@code /Level/Sections[i]/BlockStates}.
	 *
	 * @param blocks
	 *            a long array containing all the block states as Minecraft encodes them to {@code /Level/Sections[i]/BlockStates} within each
	 *            section of a chunk.
	 * @param i
	 *            The index of the block to be extracted. Since the data is mapped XZY, {@code i = x | (z<<4) | (y<<8)}.
	 * @param bitsPerIndex
	 *            The amount of bits each index has. This is to avoid redundant calculation on each call.
	 *
	 * @see #bitsPerIndex(long[])
	 */
	public static long extractFromLong(long[] blocks, int i, int bitsPerIndex) {
		int startByte = (bitsPerIndex * i) >> 6; // >> 6 equals / 64
		int endByte = (bitsPerIndex * (i + 1)) >> 6;
		// The bit within the long where our value starts. Counting from the right LSB (!).
		int startByteBit = ((bitsPerIndex * i)) & 63; // % 64 equals & 63
		int endByteBit = ((bitsPerIndex * (i + 1))) & 63;

		// Use bit shifting and & bit masking to extract bit sequences out of longs as numbers
		// -1L is the value with every bit set
		long blockIndex;
		if (startByte == endByte) {
			// Normal case: the bit string we need is within a single long
			blockIndex = (blocks[startByte] << (64 - endByteBit)) >>> (64 + startByteBit - endByteBit);
		} else if (endByteBit == 0) {
			// The bit string is exactly at the beginning of a long
			blockIndex = blocks[startByte] >>> startByteBit;
		} else {
			// The bit string is overlapping two longs
			blockIndex = ((blocks[startByte] >>> startByteBit))
					| ((blocks[endByte] << (64 - endByteBit)) >>> (startByteBit - endByteBit));
		}
		return blockIndex;
	}
}
