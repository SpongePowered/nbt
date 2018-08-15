package com.flowpowered.nbt.regionfile;

import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

import com.flowpowered.nbt.CompoundTag;
import com.flowpowered.nbt.stream.NBTInputStream;

/**
 * This helper class provides functionality to read the data of single chunks in a region/anvil file. It uses modern {@code java.nio}
 * classes like {@link Path} and {@link FileChannel} to access its data from the file. Each instance of the class represents a single file,
 * whose header will be loaded and parsed in the constructor. It will provide access to its chunks and their content, though they will have
 * to be loaded manually by calling the respective {@code load} methods. Once {@link #close()} is called, all loaded data will be freed and
 * the {@link FileChannel} used to load the data will be closed. All further usage is undefined.
 *
 * @author piegames
 */
public class RegionFile implements Closeable {

	protected final Path file;
	protected FileChannel raf;

	protected RegionChunk[] chunks;

	public RegionFile(Path file) throws IOException {
		this.file = file;

		raf = FileChannel.open(file, StandardOpenOption.READ);

		ByteBuffer locations = ByteBuffer.allocate(4096);
		raf.read(locations);
		locations.flip();
		IntBuffer locations2 = locations.asIntBuffer();

		ByteBuffer timestamps = ByteBuffer.allocate(4096);
		raf.read(timestamps);
		timestamps.flip();
		IntBuffer timestamps2 = timestamps.asIntBuffer();

		chunks = new RegionChunk[1024];
		for (int i = 0; i < 1024; i++) {
			int chunkPos = locations2.get(i) >>> 8;
			int chunkLength = locations2.get(i) & 0xFF;
			if (chunkPos > 0) {
				// i & 31 retrieves the last 5 bit which store the x coordinate
				chunks[i] = new RegionChunk(i & 31, i >> 5, chunkPos, (byte) chunkLength, timestamps2.get(i));
			}
		}
		locations.clear();
		timestamps.clear();
	}

	/** Loads all existing chunks by calling {@link RegionChunk#load()}. <b>Warning:</b> this will load almost the entire file into memory. */
	public void loadAllChunks() throws IOException {
		for (RegionChunk chunk : chunks)
			if (chunk != null)
				chunk.load();
	}

	/** Calls {@link RegionChunk#unload()} on all existing chunks */
	public void unloadAllChunks() {
		for (RegionChunk chunk : chunks)
			if (chunk != null)
				chunk.unload();
	}

	/** Unloads all loaded chunks and closes the {@link FileChannel} used to read the data. */
	@Override
	public void close() throws IOException {
		unloadAllChunks();
		raf.close();
	}

	/**
	 * Returns the chunk at the given coordinate or {@code null} if there is none
	 *
	 * @param x
	 *            the x coordinate relative to this region file's origin
	 * @param z
	 *            the z coordinate relative to this region file's origin
	 */
	public RegionChunk getChunk(int x, int z) {
		return chunks[(x & 31) | (z << 5)];
	}

	/**
	 * Returns an array of 1024 elements containing all chunks. Each position in the array corresponds to a specific chunk: all chunks are
	 * listed X first, Z second. Chunks may be {@code null} if they don't exist in the world.
	 */
	public RegionChunk[] getAllChunks() {
		return chunks;
	}

	/** List all chunks that actually exist in that region file. */
	public List<RegionChunk> listExistingChunks() {
		// TODO add back in when using Java 1.8
		// return Arrays.stream(chunks).filter(s -> s != null).collect(Collectors.toList());
		List<RegionChunk> ret = new ArrayList<>(1024);
		for (RegionChunk chunk : chunks)
			if (chunk != null)
				ret.add(chunk);
		return ret;
	}

	public class RegionChunk {
		/** The x coordinate of the chunk relative to its RegionFile's origin */
		public final int x;
		/** The z coordinate of the chunk relative to its RegionFile's origin */
		public final int z;
		/**
		 * The number of the 4096 byte sector where the chunk is located in the file. Don't forget that the first five bytes are used to store the
		 * size and compression of the chunk. The position of the first byte of NBT data is thus {@code start*4096 + 5}.
		 */
		public final int start;
		/** The number of 4096 byte sectors the chunk uses. The real length may be up to 4095 bytes smaller. */
		public final byte length;
		/** The time stamp of when this chunk got saved the last time. */
		public final int timestamp;

		protected boolean isLoaded;
		protected byte compression;
		protected int realLength;
		protected ByteBuffer data;

		RegionChunk(int x, int z, int start, byte length, int timestamp) {
			this.x = x;
			this.z = z;
			this.start = start;
			this.length = length;
			this.timestamp = timestamp;
		}

		/**
		 * Load the chunk's data and extract further information like its real size and compression method. This will keep all sectors of the file
		 * that belong to this chunk in memory in form of a {@link ByteBuffer}. If the chunk has already been loaded it will reload it and overwrite
		 * the data.
		 */
		public void load() throws IOException {
			data = ByteBuffer.allocate(4096 * length);
			raf.read(data, 4096 * start);
			data.flip();

			realLength = data.getInt(0) - 1;
			compression = data.get(5);

			isLoaded = true;
		}

		/** Unloads all chunk data if it has been loaded */
		public void unload() {
			data.clear();
			data = null;
			isLoaded = false;
		}

		/**
		 * The real length of the NBT data in this chunk in bytes.
		 *
		 * @throws IllegalStateException
		 *             if the chunk hasn't been loaded yet
		 */
		public int getRealLength() {
			if (!isLoaded)
				throw new IllegalStateException("Chunk must be loaded before reading");
			return realLength;
		}

		/**
		 * Returns the compression method used in this chunk as specified by the format. This value corresponds to the compression that an
		 * {@link NBTInputStream} takes in its constructor.
		 *
		 * @throws IllegalStateException
		 *             if the chunk hasn't been loaded yet
		 */
		public byte getCompression() {
			if (!isLoaded)
				throw new IllegalStateException("Chunk must be loaded before reading");
			return compression;
		}

		/**
		 * Returns the {@link ByteBuffer} containing all the data in this chunk, including the five bytes before the actual NBT data.
		 *
		 * @throws IllegalStateException
		 *             if the chunk hasn't been loaded yet
		 */
		public ByteBuffer getData() {
			if (!isLoaded)
				throw new IllegalStateException("Chunk must be loaded before reading");
			return data;
		}

		/**
		 * Open an {@link NBTInputStream} for reading.
		 *
		 * @throws IllegalStateException
		 *             if the chunk hasn't been loaded yet
		 */
		public NBTInputStream read() throws IOException {
			if (!isLoaded)
				throw new IllegalStateException("Chunk must be loaded before reading");
			return new NBTInputStream(new ByteArrayInputStream(data.array(), 5, realLength), compression);
		}

		/**
		 * Reads the NBT chunk data and returns it. The normally nameless root tag will be renamed to "chunk".
		 *
		 * @throws IllegalStateException
		 *             if the chunk hasn't been loaded yet
		 */
		public CompoundTag readTag() throws IOException {
			CompoundTag tag = null;
			try (NBTInputStream nbtIn = read();) {
				tag = new CompoundTag("chunk", ((CompoundTag) nbtIn.readTag()).getValue());
			}
			return tag;
		}
	}
}