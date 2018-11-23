package com.flowpowered.nbt;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;

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
		RegionFile file = new RegionFile(Paths.get(getClass().getResource("/r.1.3.mca").toURI()));
		for (Chunk chunk : file) {
			if (chunk != null)
				chunk.readTag();
		}
	}

	@Test
	public void testCreateNew() throws IOException {
		File file = folder.newFile();
		file.delete();
		RegionFile.createNew(file.toPath());
		file.delete();
		RegionFile rf = RegionFile.open(folder.newFolder().toPath().resolve("test").resolve("test.mca"));
		rf.writeChanges();
		assertEquals(4096 * 2, Files.size(rf.getPath()));
	}
}
