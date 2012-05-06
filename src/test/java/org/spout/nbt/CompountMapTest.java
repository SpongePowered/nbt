/*
 * This file is part of SpoutNBT <http://www.spout.org/>.
 *
 * SpoutNBT is licensed under the SpoutDev License Version 1.
 *
 * SpoutNBT is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the SpoutDev License Version 1.
 *
 * SpoutNBT is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License,
 * the MIT license and the SpoutDev License Version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://www.spout.org/SpoutDevLicenseV1.txt> for the full license,
 * including the MIT license.
 */
package org.spout.nbt;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.junit.Before;
import org.junit.Test;

public class CompountMapTest {
	List<Tag> sorted;
	List<Tag> random;
	List<Tag> reverse;

	@Before
	public void setupLists() {
		random = new ArrayList<Tag>();

		Random r = new Random();

		for (int i = 0; i < 20; i++) {
			random.add(new LongTag(Integer.toHexString(r.nextInt()), r.nextLong()));
		}

		sorted = new ArrayList<Tag>(random);
		Collections.sort(sorted);

		reverse = new ArrayList<Tag>(random);
		Collections.sort(reverse, Collections.reverseOrder());
	}

	@Test
	public void printLists() {
		printList("Random", random);
		printList("Sorted", sorted);
		printList("Reverse", reverse);
	}

	private void printList(String type, List<Tag> list) {
		StringBuilder sb = new StringBuilder();
		sb.append("List: " + type + "\n");
		for (Tag t : list) {
			sb.append(t.getName() + "\n");
		}
		System.out.println(sb.toString());
	}

	@Test
	public void preserveOrder() {
		CompoundMap tag = new CompoundMap(random, false, false);

		assertEquals("Tag setup", tag, random);

		tag = new CompoundMap(false, false);

		for (Tag t : random) {
			tag.put(t);
		}

		assertEquals("Tag setup", tag, random);
	}

	@Test
	public void sorted() {
		CompoundMap tag = new CompoundMap(random, true, false);

		assertEquals("Tag setup", tag, sorted);

		tag = new CompoundMap(true, false);

		for (Tag t : random) {
			tag.put(t);
		}

		assertEquals("Tag setup", tag, sorted);
	}

	@Test
	public void reverseSorted() {
		CompoundMap tag = new CompoundMap(random, true, true);

		assertEquals("Tag setup", tag, reverse);

		tag = new CompoundMap(true, true);

		for (Tag t : random) {
			tag.put(t);
		}

		assertEquals("Tag setup", tag, reverse);
	}

	private void assertEquals(String message, Iterable<Tag> a, Iterable<Tag> b) {
		Iterator<Tag> iterA = a.iterator();
		Iterator<Tag> iterB = b.iterator();
		while (iterA.hasNext() && iterB.hasNext()) {
			Tag currentA = iterA.next();
			Tag currentB = iterB.next();
			assertTrue(message , currentA.equals(currentB));
		}
		assertFalse("Maps had different lengths", iterA.hasNext() || iterB.hasNext());
	}
}
