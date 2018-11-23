package com.flowpowered.nbt.regionfile;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

/**
 * This helper class provides functionality to read the data of single chunks in a region/anvil file. It uses modern {@code java.nio}
 * classes like {@link Path} and {@link FileChannel} to access its data from the file. Each instance of the class represents a single file,
 * which will be loaded completely in the constructor. Warning: typical Minecraft region files may take up to several MiB of space and the
 * theoretical maximum size is at one GiB (1024 region files take up at most 255 sectors à 4096 bytes). <br/>
 * Once the object has been created, it will close the opened {@link FileChannel}. No data will be read anymore. It will be assumed that the
 * file will not be modified externally in any ways. All changes to the objects are kept in memory and only written back to the file upon
 * calling {@link #writeChanges()}, in which case a new file channel is opened.
 *
 * @author piegames
 */
public class RegionFile implements Iterable<Chunk> {

	protected final Path	file;

	protected final Chunk[]	chunks;
	protected boolean[]		chunksUpToDate;

	protected ByteBuffer	locations;
	protected IntBuffer		locations2;
	protected ByteBuffer	timestamps;
	protected IntBuffer		timestamps2;
	protected ShortBuffer	sectorCounts;
	protected List<Boolean>	sectorUsed;

	/**
	 * Create a new RegionFile object representing the region file at the given path and load it into memory.
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
		FileChannel raf = FileChannel.open(file, StandardOpenOption.READ);

		locations = ByteBuffer.allocate(4096);
		raf.read(locations);
		locations.flip();
		locations2 = locations.asIntBuffer();

		timestamps = ByteBuffer.allocate(4096);
		raf.read(timestamps);
		timestamps.flip();
		timestamps2 = timestamps.asIntBuffer();

		chunks = new Chunk[32 * 32];
		chunksUpToDate = new boolean[32 * 32];
		for (int i = 0; i < 1024; i++) {
			int chunkPos = locations2.get(i) >>> 8;
			int chunkLength = locations2.get(i) & 0xFF;
			if (chunkPos > 0) {
				/* i & 31 retrieves the last 5 bit which store the x coordinate */
				chunks[i] = new Chunk(i & 31, i >> 5, timestamps2.get(i), raf, chunkPos, chunkLength);
			} else
				chunks[i] = null;
			chunksUpToDate[i] = true;
		}
	}

	/**
	 * Returns the {@link Chunk} at the given coordinate
	 *
	 * @param x
	 *            the x coordinate relative to this region file's origin
	 * @param z
	 *            the z coordinate relative to this region file's origin
	 * @return the chunk at that coordinate or {@code null} if the chunk does not exist
	 * @author piegames
	 */
	public Chunk getChunk(int x, int z) {
		return chunks[(x & 31) | (z << 5)];
	}

	public Chunk getChunk(int i) {
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
	 * @author piegames
	 */
	public synchronized void setChunk(int x, int z, Chunk chunk) {
		setChunk((x & 31) | (z << 5), chunk);
	}

	public synchronized void setChunk(int i, Chunk chunk) {
		chunks[i] = chunk;
		chunksUpToDate[i] = false;
	}

	/**
	 * Write all chunks that got changed to disk, update the file's header (chunk locations and timestamps) and truncate the file at the end
	 * 
	 * @author piegames
	 */
	public synchronized void writeChanges() throws IOException {
		try (FileChannel raf = FileChannel.open(file, StandardOpenOption.WRITE, StandardOpenOption.CREATE);) {
			BitSet usedSectors = new BitSet();
			usedSectors.set(0, 2); /* Set the first two sectors as used since they always ares */
			List<Integer> toSave = new ArrayList<>();
			for (int i = 0; i < 32 * 32; i++) {
				if (!chunksUpToDate[i]) {
					toSave.add(i);
					continue;
				}
				if (chunks[i] == null)
					continue;
				int chunkPos = locations2.get(i) >>> 8;
				int chunkLength = locations2.get(i) & 0xFF;
				usedSectors.set(chunkPos, chunkPos + chunkLength);
			}
			/* Iterate through all changed chunks and try to fit them in somewhere */
			for (Integer chunkPos : toSave) {
				if (chunks[chunkPos].data == null) {
					/* Position zero, length zero */
					locations2.put(chunks[chunkPos].z << 5 | chunks[chunkPos].x, 0);
				} else {
					int length = 0;
					int start = 0;
					/* Increase start until we found a solid place to put our data */
					while (length < chunks[chunkPos].getSectorLength()) {
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
						if (chunks[chunkPos] != null) {
							raf.position(start * 4096);
							raf.write(chunks[chunkPos].data);
							timestamps2.put(chunkPos, chunks[chunkPos].timestamp);
						}
						chunksUpToDate[chunkPos] = true;
					}
					locations2.put(chunks[chunkPos].z << 5 | chunks[chunkPos].x, start << 8 | length);
					usedSectors.set(start, start + length);
				}
			}
			raf.position(0);
			raf.write(locations);
			raf.write(timestamps);
			locations.flip();
			timestamps.flip();

			raf.truncate(4096 * usedSectors.previousSetBit(usedSectors.size()) + 4096);
		}
	}

	/** {@inheritDoc} */
	@Override
	public Iterator<Chunk> iterator() {
		return new Iterator<Chunk>() {
			int i = 0;

			@Override
			public boolean hasNext() {
				return i < 32 * 32;
			}

			@Override
			public Chunk next() {
				return chunks[i++];
			}
		};
	}

	/** Get the path this file is associated with. It will never change over time. */
	public Path getPath() {
		return file;
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
			return new RegionFile(file);
		}
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
}