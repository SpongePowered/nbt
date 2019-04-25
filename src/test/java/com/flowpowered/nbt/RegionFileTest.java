package com.flowpowered.nbt;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.flowpowered.nbt.regionfile.Chunk;
import com.flowpowered.nbt.regionfile.RegionFile;

public class RegionFileTest {

	@Rule
	public TemporaryFolder folder = new TemporaryFolder();

	/**
	 * Test reading the NBT data in a region file
	 *
	 * @throws URISyntaxException
	 * @throws IOException
	 */
	@Test
	public void testRead() throws IOException, URISyntaxException {
		try (RegionFile file = new RegionFile(Paths.get(getClass().getResource("/r.1.3.mca").toURI()));) {
			for (int i : file.listChunks()) {
				Chunk chunk = file.loadChunk(i);
				if (chunk != null)
					chunk.readTag();
			}
		}
	}

	@Test
	public void testCreateNew() throws IOException {
		File file = folder.newFile();
		file.delete();
		RegionFile.createNew(file.toPath()).close();
		file.delete();
		RegionFile rf = RegionFile.open(folder.newFolder().toPath().resolve("test").resolve("test.mca"));
		rf.writeChunks(new HashMap<>());
		assertEquals(4096 * 2, Files.size(rf.getPath()));
		rf.close();
	}
}
