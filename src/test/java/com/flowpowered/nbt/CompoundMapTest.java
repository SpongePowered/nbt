/*
 * This file is part of Flow NBT, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2011 Flow Powered <https://flowpowered.com/>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.flowpowered.nbt;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CompoundMapTest {
    List<Tag<?>> sorted;
    List<Tag<?>> random;
    List<Tag<?>> reverse;

    @Before
    public void setupLists() {
        random = new ArrayList<Tag<?>>();

        Random r = new Random();

        for (int i = 0; i < 20; i++) {
            random.add(new LongTag(Integer.toHexString(r.nextInt()), r.nextLong()));
        }

        sorted = new ArrayList<Tag<?>>(random);
        Collections.sort(sorted);

        reverse = new ArrayList<Tag<?>>(random);
        Collections.sort(reverse, Collections.reverseOrder());
    }

    @Test
    public void printLists() {
        printList("Random", random);
        printList("Sorted", sorted);
        printList("Reverse", reverse);
    }

    private void printList(String type, List<Tag<?>> list) {
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

    private void assertEquals(String message, Iterable<Tag<?>> a, Iterable<Tag<?>> b) {
        Iterator<Tag<?>> iterA = a.iterator();
        Iterator<Tag<?>> iterB = b.iterator();
        while (iterA.hasNext() && iterB.hasNext()) {
            Tag currentA = iterA.next();
            Tag currentB = iterB.next();
            assertTrue(message, currentA.equals(currentB));
        }
        assertFalse("Maps had different lengths", iterA.hasNext() || iterB.hasNext());
    }
}
