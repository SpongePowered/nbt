package com.flowpowered.nbt;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.flowpowered.nbt.regionfile.RegionFile;
import com.flowpowered.nbt.regionfile.RegionFile.RegionChunk;

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
		try (RegionFile file = new RegionFile(Paths.get(getClass().getResource("/r.1.3.mca").toURI()))) {
			for (RegionChunk chunk : file) {
				if (chunk.getData() != null)
					chunk.getData().readTag();
			}
		}
	}

	@Test
	public void testCreateNew() throws IOException {
		File file = folder.newFile();
		file.delete();
		new RegionFile(file.toPath()).close();
		file.delete();
	}
}
