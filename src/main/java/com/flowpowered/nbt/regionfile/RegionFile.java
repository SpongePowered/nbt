package com.flowpowered.nbt.regionfile;

import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import com.flowpowered.nbt.regionfile.RegionFile.RegionChunk;

/**
 * This helper class provides functionality to read the data of single chunks in a region/anvil file. It uses modern {@code java.nio}
 * classes like {@link Path} and {@link FileChannel} to access its data from the file. Each instance of the class represents a single file,
 * which will be loaded completely in the constructor. Warning: typical Minecraft region files may take up to several MiB of space and the
 * theoretical maximum size is at one GiB (1024 region files take up at most 255 sectors à 4096 bytes). <br/>
 * Once the object has been created, it will keep the {@link FileChannel} open in case of write operations. No data will be read anymore. It
 * will be assumed that the file will not be modified externally in any ways. All changes to the objects are kept in memory and only written
 * back to the file upon calling {@link #writeChanges()}. <br/>
 * Once the file has been closed, it is unlinked from the physical file and should not be used anymore, even if the data remains in memory
 * until garbage-collected.
 *
 * @author piegames
 */
public class RegionFile implements Closeable, Iterable<RegionChunk> {

	protected final Path			file;
	protected FileChannel			raf;

	protected final RegionChunk[]	chunks;

	protected ByteBuffer			locations;
	protected IntBuffer				locations2;
	protected ByteBuffer			timestamps;
	protected IntBuffer				timestamps2;
	protected ShortBuffer			sectorCounts;
	protected List<Boolean>			sectorUsed;

	/**
	 * Create a new RegionFile object representing the region file at the given path and load it into memory. If the file does not exist, it
	 * will be created.
	 */
	public RegionFile(Path file) throws IOException {
		this.file = Objects.requireNonNull(file);

		boolean exists = Files.exists(file);
		Files.createDirectories(file.getParent());
		raf = FileChannel.open(file, StandardOpenOption.READ, StandardOpenOption.WRITE, StandardOpenOption.CREATE);

		locations = ByteBuffer.allocate(4096);
		if (exists)
			raf.read(locations);
		else
			locations.put(new byte[4096]);
		locations.flip();
		locations2 = locations.asIntBuffer();

		timestamps = ByteBuffer.allocate(4096);
		if (exists)
			raf.read(timestamps);
		else
			timestamps.put(new byte[4096]);
		timestamps.flip();
		timestamps2 = timestamps.asIntBuffer();

		chunks = new RegionChunk[1024];
		for (int i = 0; i < 1024; i++) {
			int chunkPos = locations2.get(i) >>> 8;
			int chunkLength = locations2.get(i) & 0xFF;
			if (exists && chunkPos > 0) {
				// i & 31 retrieves the last 5 bit which store the x coordinate
				chunks[i] = new RegionChunk(i & 31, i >> 5, new Chunk(timestamps2.get(i), raf, chunkPos, chunkLength));
			} else
				chunks[i] = new RegionChunk(i & 31, i >> 5, null);
		}
	}

	/**
	 * Closes the {@link FileChannel} used to read the data, but keeps all chunk data referenced and thus in memory.
	 */
	@Override
	public void close() throws IOException {
		raf.close();
	}

	/**
	 * Returns the {@link RegionChunk} at the given coordinate
	 *
	 * @param x
	 *            the x coordinate relative to this region file's origin
	 * @param z
	 *            the z coordinate relative to this region file's origin
	 */
	public RegionChunk getChunk(int x, int z) {
		return chunks[(x & 31) | (z << 5)];
	}

	public RegionChunk getChunk(int i) {
		return chunks[i];
	}

	/**
	 * Set a chunk at the given coordinate
	 * 
	 * @param x
	 *            the x coordinate relative to this region file's origin
	 * @param z
	 *            the z coordinate relative to this region file's origin
	 * @param chunk
	 *            The chunk to set. Use {@code null} to remove it.
	 */
	public synchronized void setChunk(int x, int z, Chunk chunk) {
		chunks[(x & 31) | (z << 5)].setData(chunk);
	}

	public synchronized void setChunk(int i, Chunk chunk) {
		chunks[i].setData(chunk);
	}

	/** Write all chunks that got changed to disk, update the file's header (chunk locations and timestamps) and truncate the file at the end */
	public synchronized void writeChanges() throws IOException {
		BitSet usedSectors = new BitSet();
		usedSectors.set(0, 2); /* Set the first two sectors as used since they always ares */
		List<RegionChunk> toSave = new ArrayList<>();
		for (int i = 0; i < 32 * 32; i++) {
			if (!chunks[i].isUpToDate()) {
				toSave.add(chunks[i]);
				continue;
			}
			if (chunks[i].data == null)
				continue;
			int chunkPos = locations2.get(i) >>> 8;
			int chunkLength = locations2.get(i) & 0xFF;
			usedSectors.set(chunkPos, chunkPos + chunkLength);
		}
		/* Iterate through all changed chunks and try to fit them in somewhere */
		for (RegionChunk chunk : toSave) {
			if (chunk.data == null) {
				/** Position zero, length zero */
				locations2.put(chunk.z << 5 | chunk.x, 0);
			} else {
				int length = 0;
				int start = 0;
				/* Increase start until we found a solid place to put our data */
				while (length < chunk.data.getSectorLength()) {
					if (!usedSectors.get(start + length)) {
						length++;
					} else {
						start = usedSectors.nextClearBit(start + length);
						length = 0;
					}
				}
				if (length != chunk.data.getSectorLength())
					throw new InternalError("TODO remove me"); // TODO remove me
				if (length > 255)
					throw new IOException("Chunks are limited to a length of maximum 255 sectors, or ~1MiB");
				chunk.writeData(start);
				locations2.put(chunk.z << 5 | chunk.x, start << 8 | length);
				usedSectors.set(start, start + length);
			}
		}
		raf.position(0);
		raf.write(locations);
		raf.write(timestamps);
		locations.flip();
		timestamps.flip();

		raf.truncate(4096 * usedSectors.previousSetBit(usedSectors.size()));
	}

	/**
	 * This class represents one chunk in a region file. It is directly tied to the {@link RegionFile} it belongs to. It is only a wrapper for
	 * the actual {@link Chunk} containing the data to track changes. If the data is {@code null}, this means that there is no chunk at this
	 * coordinate.
	 * 
	 * @author piegames
	 */
	public final class RegionChunk {

		/** The x coordinate of the chunk relative to its RegionFile's origin */
		public final int			x;
		/** The z coordinate of the chunk relative to its RegionFile's origin */
		public final int			z;

		private Chunk				data;
		private volatile boolean	upToDate;

		private RegionChunk(int x, int z, Chunk data) {
			if (x < 0 || z < 0 || x > 32 || z > 32)
				throw new IllegalArgumentException("Coordinates must be in range [0..32), but were x=" + x + ", z=" + z + ")");
			this.x = x;
			this.z = z;
			this.data = data;
			upToDate = true;
		}

		/** Returns whether the data has been written back to disk since its last change. */
		public synchronized boolean isUpToDate() {
			return upToDate;
		}

		/** May return {@code null} if the chunk does not exist */
		public Chunk getData() {
			return data;
		}

		/** Set the data to {@code null} to delete this chunk */
		public synchronized void setData(Chunk data) {
			this.data = data;
			upToDate = false;
		}

		private synchronized void writeData(int start) throws IOException {
			if (data != null) {
				raf.position(start * 4096);
				raf.write(data.data);
				timestamps2.put(z << 5 | x, data.timestamp);
			}
			upToDate = true;
		}
	}

	@Override
	public Iterator<RegionChunk> iterator() {
		return new Iterator<RegionFile.RegionChunk>() {
			int i = 0;

			@Override
			public boolean hasNext() {
				return i < 32 * 32;
			}

			@Override
			public RegionChunk next() {
				return chunks[i++];
			}
		};
	}

	public Path getPath() {
		return file;
	}
}