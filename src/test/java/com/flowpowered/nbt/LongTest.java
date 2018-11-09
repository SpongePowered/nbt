package com.flowpowered.nbt;

import static org.junit.Assert.assertEquals;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.LongBuffer;
import java.util.Random;

import org.junit.Test;

import com.flowpowered.nbt.regionfile.Chunk;

public class LongTest {

	@Test
	public void test() {
		for (int BITS = 4; BITS <= 12; BITS++) {
			Random random = new Random(1234);

			// Fill a byte buffer with random data
			ByteBuffer buffer = ByteBuffer.wrap(new byte[BITS * 64 * 8]);
			buffer.order(ByteOrder.BIG_ENDIAN);
			random.nextBytes(buffer.array());

			// Convert it to a long buffer
			LongBuffer data = buffer.asLongBuffer();
			data.rewind();
			long[] longData = new long[BITS * 64];
			data.get(longData);

			// Control data: Convert all bytes to a binary string and zero-pad them, append them to a large bit string
			// Slicing the bit string into equal length substrings will give the control data.
			// Add a double reverse because Mojang does silly stuff sometimes.
			StringBuffer number = new StringBuffer();
			for (long l : longData)
				number.append(convertLong(Long.reverse(l)));
			for (int i = 0; i < BITS * 64; i++) {
				// Compare conversion with control data
				assertEquals(Long.parseLong(new StringBuilder(number.substring(i * BITS, i * BITS + BITS)).reverse().toString(), 2),
						Chunk.extractFromLong(longData, i, BITS));
			}
		}
	}

	/** Convert long to binary string and zero pad it */
	private static String convertLong(long l) {
		String s = Long.toBinaryString(l);
		// Fancy way of zero padding :)
		s = "0000000000000000000000000000000000000000000000000000000000000000".substring(s.length()) + s;
		return s;
	}
}
