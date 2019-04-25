package com.flowpowered.nbt.regionfile;

import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.BitSet;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * This helper class provides functionality to read the data of single chunks in a region/anvil file. It uses modern {@code java.nio}
 * classes like {@link Path} and {@link FileChannel} to access its data from the file. Each instance of the class represents a single file,
 * whose header will be loaded in the constructor.
 *
 * @author piegames
 */
public class RegionFile implements Closeable {

	protected final Path	file;
	protected FileChannel	raf;

	protected ByteBuffer	locations;
	protected IntBuffer		locations2;
	protected ByteBuffer	timestamps;
	protected IntBuffer		timestamps2;

	/**
	 * Create a new RegionFile object representing the region file at the given path and load it's header to memory.
	 * 
	 * @throws IllegalArgumentException
	 *             if the file is smaller than 4kiB
	 * @throws NoSuchFileException
	 *             if the file does not exist
	 * @see #open(Path)
	 * @author piegames
	 */
	public RegionFile(Path file) throws IOException {
		this.file = Objects.requireNonNull(file);

		if (!Files.exists(file))
			throw new NoSuchFileException(file.toString());
		if (Files.size(file) < 4096 * 2)
			throw new IllegalArgumentException("File size must be at least 4kiB, is this file corrupt?");
		raf = FileChannel.open(file, StandardOpenOption.READ, StandardOpenOption.WRITE);

		locations = ByteBuffer.allocate(4096);
		raf.read(locations);
		locations.flip();
		locations2 = locations.asIntBuffer();

		timestamps = ByteBuffer.allocate(4096);
		raf.read(timestamps);
		timestamps.flip();
		timestamps2 = timestamps.asIntBuffer();
	}

	/**
	 * Load the {@link Chunk} at the given coordinate
	 * 
	 * @see #coordsToPosition(int, int)
	 * @return the chunk at that coordinate or {@code null} if the chunk does not exist
	 * @throws IOException
	 * @author piegames
	 */
	public Chunk loadChunk(int x, int z) throws IOException {
		return loadChunk(coordsToPosition(x, z));
	}

	/** @see #loadChunk(int, int) */
	public Chunk loadChunk(int i) throws IOException {
		int chunkPos = locations2.get(i) >>> 8;
		int chunkLength = locations2.get(i) & 0xFF;
		if (chunkPos > 0) {
			/* i & 31 retrieves the last 5 bit which store the x coordinate */
			return new Chunk(i & 31, i >> 5, timestamps2.get(i), raf, chunkPos, chunkLength);
		}
		return null;
	}

	/**
	 * Tell if the file contains a chunk at this position.
	 * 
	 * @return {@code true} if there is a chunk at this position
	 * @see #coordsToPosition(int, int)
	 */
	public boolean hasChunk(int x, int z) {
		return hasChunk((x & 31) | (z << 5));
	}

	/** @see #hasChunk(int, int) */
	public boolean hasChunk(int i) {
		return (locations2.get(i) >>> 8) > 0;
	}

	/**
	 * Same as {@link #listChunks()}, but as stream.
	 */
	public Stream<Integer> streamChunks() {
		return IntStream.range(0, 32 * 32).filter(pos -> hasChunk(pos))
				.boxed()
				.sorted(Comparator.comparingInt(i -> locations2.get(i) >>> 8));
	}

	/**
	 * List the positions of all chunks that exist in this file sorted by their their appearance order in the file. Use this to read all chunks
	 * in their sequential order to speed up seek times.
	 * 
	 * @see #coordsToPosition(int, int)
	 */
	public List<Integer> listChunks() {
		return streamChunks().collect(Collectors.toList());
	}

	/**
	 * Write all given chunks to disk, update the file's header (chunk locations and timestamps) and truncate the file at the end
	 * 
	 * @param changedChunks
	 *            {@link HashMap} of all changes to write. Each key is the position of one changed chunk (use
	 *            {@link #coordsToPosition(int, int)} to calculate the key from a coordinate). The map may contain {@code null} values,
	 *            indicating that the chunk should be removed from the file.
	 * @author piegames
	 */
	public void writeChunks(HashMap<Integer, Chunk> changedChunks) throws IOException {
		synchronized (raf) {
			/* Mark all 4kib sectors in the file if they are used. */
			BitSet usedSectors = new BitSet();
			/* Set the first two sectors as used since they always are (by the header) */
			usedSectors.set(0, 2);

			/* Mark the currently used sectors, but omit those that are going to be deleted or overwritten. */
			for (int i = 0; i < 32 * 32; i++) {
				int chunkPos = locations2.get(i) >>> 8;
				int chunkLength = locations2.get(i) & 0xFF;
				if (chunkLength > 0 && !changedChunks.containsKey(i))
					usedSectors.set(chunkPos, chunkPos + chunkLength);
			}

			/* Iterate through all changed chunks and try to fit them in somewhere */
			for (Integer chunkPos : changedChunks.keySet()) {
				Chunk chunk = changedChunks.get(chunkPos);
				if (chunk == null) {
					/* Position zero, length zero */
					locations2.put(chunkPos, 0);
				} else {
					int length = 0;
					int start = 0;
					/* Increase start until we found a solid place to put our data */
					while (length < chunk.getSectorLength()) {
						if (!usedSectors.get(start + length)) {
							length++;
						} else {
							start = usedSectors.nextClearBit(start + length);
							length = 0;
						}
					}
					if (length > 255)
						throw new IOException("Chunks are limited to a length of maximum 255 sectors, or ~1MiB");
					{ /* Write the chunk to disk */
						raf.position(start * 4096);
						raf.write(chunk.data);
						timestamps2.put(chunkPos, chunk.timestamp);
					}
					locations2.put(chunkPos, start << 8 | length);
					usedSectors.set(start, start + length);
				}
			}
			/* Write updated header */
			raf.position(0);
			raf.write(locations);
			raf.write(timestamps);
			locations.flip();
			timestamps.flip();

			raf.truncate(4096 * usedSectors.previousSetBit(usedSectors.size()) + 4096);
		}
		changedChunks.clear();
	}

	/** Get the path this file is associated with. It will never change over time. */
	public Path getPath() {
		return file;
	}

	@Override
	public void close() throws IOException {
		raf.close();
	}

	/**
	 * Create a new region file by writing an empty header to it.
	 * 
	 * @author piegames
	 */
	public static RegionFile createNew(Path file) throws IOException {
		try (FileChannel raf = FileChannel.open(file, StandardOpenOption.WRITE, StandardOpenOption.CREATE_NEW);) {
			/* Write empty header */
			raf.write(ByteBuffer.wrap(new byte[2 * 4096]));
		}
		return new RegionFile(file);
	}

	/**
	 * Open an existing region file, creating it if it does not exist
	 * 
	 * @author piegames
	 */
	public static RegionFile open(Path file) throws IOException {
		if (Files.exists(file))
			return new RegionFile(file);
		else {
			Files.createDirectories(file.getParent());
			return createNew(file);
		}
	}

	/**
	 * Convert a coordinate into a position index.
	 * 
	 * @param x
	 *            The x position of the chunk in chunk coordinates (1 unit <=> 16 blocks). The coordinate should be relative to the region
	 *            file's position, but using the world's origin works fine as well.
	 * @param z
	 *            The z position of the chunk in chunk coordinates (1 unit <=> 16 blocks). The coordinate should be relative to the region
	 *            file's position, but using the world's origin works fine as well.
	 * @return The index of this chunk in the file. This is a number between 0 (inclusive) and 32*32 (exclusive). The coordinate is flattened in
	 *         x-z-order.
	 */
	public static int coordsToPosition(int x, int z) {
		return (x & 31) | ((z & 31) << 5);
	}
}