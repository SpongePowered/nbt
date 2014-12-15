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

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Represents a single NBT tag.
 */
public abstract class Tag<T> implements Comparable<Tag<?>> {
    /**
     * The name of this tag.
     */
    private final String name;
    private final TagType type;

    /**
     * Creates the tag with no name.
     */
    public Tag(TagType type) {
        this(type, "");
    }

    /**
     * Creates the tag with the specified name.
     *
     * @param name The name.
     */
    public Tag(TagType type, String name) {
        this.name = name;
        this.type = type;
    }

    /**
     * Gets the name of this tag.
     *
     * @return The name of this tag.
     */
    public final String getName() {
        return name;
    }

    /**
     * Returns the type of this tag
     *
     * @return The type of this tag.
     */
    public TagType getType() {
        return type;
    }

    /**
     * Gets the value of this tag.
     *
     * @return The value of this tag.
     */
    public abstract T getValue();

    /**
     * Clones a Map<String, Tag>
     *
     * @param map the map
     * @return a clone of the map
     */
    public static Map<String, Tag<?>> cloneMap(Map<String, Tag<?>> map) {
        if (map == null) {
            return null;
        }

        Map<String, Tag<?>> newMap = new HashMap<String, Tag<?>>();
        for (Entry<String, Tag<?>> entry : map.entrySet()) {
            newMap.put(entry.getKey(), entry.getValue().clone());
        }
        return newMap;
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Tag)) {
            return false;
        }
        Tag<?> tag = (Tag<?>) other;
        return getValue().equals(tag.getValue()) && getName().equals(tag.getName());
    }

    @Override
    public int compareTo(Tag other) {
        if (equals(other)) {
            return 0;
        } else {
            if (other.getName().equals(getName())) {
                throw new IllegalStateException("Cannot compare two Tags with the same name but different values for sorting");
            } else {
                return getName().compareTo(other.getName());
            }
        }
    }

    /**
     * Clones the Tag
     *
     * @return the clone
     */
    public abstract Tag<T> clone();
}
