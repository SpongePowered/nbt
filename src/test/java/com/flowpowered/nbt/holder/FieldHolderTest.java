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
package com.flowpowered.nbt.holder;

import org.junit.Before;
import org.junit.Test;

import com.flowpowered.nbt.CompoundMap;
import com.flowpowered.nbt.CompoundTag;
import com.flowpowered.nbt.StringTag;

import static org.junit.Assert.assertEquals;

/**
 * Test for {@link FieldHolder}
 */
public class FieldHolderTest {
    private ExampleHolder subject;

    @Before
    public void setUp() {
        subject = new ExampleHolder();
        CompoundMap map = new CompoundMap();
        map.put(new StringTag("name", "helloworld"));
        CompoundTag mapTag = new CompoundTag("", map);
        subject.load(mapTag);
    }

    private static class ExampleHolder extends FieldHolder {
        public final FieldValue<String> name = FieldValue.from("name", new BasicTagField<String>(StringTag.class)),
                unassignedDefault = FieldValue.from("unassigned", new BasicTagField<String>(StringTag.class), "value-here");

        public ExampleHolder() {
            addFields(name, unassignedDefault);
        }
    }

    @Test
    public void testBasicLoad() {
        assertEquals("helloworld", subject.name.get());
        assertEquals("value-here", subject.unassignedDefault.get());
    }

    @Test
    public void testDefaultValue() {
        assertEquals("value-here", subject.unassignedDefault.get());
    }
}
