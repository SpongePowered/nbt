package com.flowpowered.nbt.stream;

import static org.junit.Assert.assertArrayEquals;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.Test;

import com.flowpowered.nbt.Tag;

public class NBTInputStreamTest {

	/** Read a simple NBT file and compare it to a previous result */
	@Test
	public void testNBT() throws IOException, URISyntaxException {
		try (NBTInputStream in = new NBTInputStream(getClass().getResourceAsStream("/level.dat"), NBTInputStream.GZIP_COMPRESSION)) {
			Tag<?> tag = in.readTag();
			assertArrayEquals(
					Files.readAllLines(Paths.get(getClass().getResource("/level.txt").toURI())).toArray(new String[] {}),
					tag.toString().split("\r\n"));
		}
	}
}