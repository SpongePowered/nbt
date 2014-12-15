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

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class CompoundMap implements Map<String, Tag<?>>, Iterable<Tag<?>> {
    private final Map<String, Tag<?>> map;
    private final boolean sort;
    private final boolean reverse;

    /**
     * Creates an empty CompoundMap backed by a HashMap.
     */
    public CompoundMap() {
        this(null, false, false);
    }

    /**
     * Creates a CompoundMap back by a LinkedHashMap, so insertion order is preserved.<br> <br> The map is initialised using the values given in the List.
     *
     * @param initial the initial values for the CompoundMap
     */
    public CompoundMap(List<Tag<?>> initial) {
        this(initial, false, false);
    }

    /**
     * Creates a CompoundMap back by a LinkedHashMap, so insertion order is preserved.<br> <br> The map is initialised using the values given in the Map.
     *
     * @param initial the initial values for the CompoundMap
     */
    public CompoundMap(Map<String, Tag<?>> initial) {
        this(initial.values(), false, false);
    }

    /**
     * Creates a CompoundMap back by a LinkedHashMap, so insertion order is preserved.<br> <br> The map is initialised using the values given in the HashMap.  The constructor is included for backward
     * compatibility, it is recommended to use the one that takes Map<String, Tag> instead.
     *
     * @param initial the initial values for the CompoundMap
     */
    @Deprecated
    public CompoundMap(HashMap<String, Tag<?>> initial) {
        this((Map<String, Tag<?>>) initial);
    }

    /**
     * Creates a CompoundMap using the same element ordering rules as in the given CompoundMap.<br> <br> The map is initialised using the values given in the CompoundMap.
     *
     * @param initial the initial values for the CompoundMap
     */
    public CompoundMap(CompoundMap initial) {
        this(initial.values(), initial.sort, initial.reverse);
    }

    /**
     * Creates an empty CompoundMap.<br> <br>
     *
     * @param sort elements are ordered in alphabetical ordering
     * @param reverse elements are ordered in reverse alphabetical ordering, when sort is true
     */
    public CompoundMap(boolean sort, boolean reverse) {
        this(null, sort, reverse);
    }

    /**
     * Creates an empty CompoundMap which is initialised using the given values<br> <br>
     *
     * @param initial the initial values
     * @param sort elements are ordered in alphabetical ordering
     * @param reverse elements are ordered in reverse alphabetical ordering, when sort is true
     */
    public CompoundMap(Iterable<Tag<?>> initial, boolean sort, boolean reverse) {
        if (reverse) {
            this.sort = true;
        } else {
            this.sort = sort;
        }
        this.reverse = reverse;
        if (!sort) {
            this.map = new LinkedHashMap<String, Tag<?>>();
        } else {
            if (reverse) {
                this.map = new TreeMap<String, Tag<?>>(Collections.reverseOrder());
            } else {
                this.map = new TreeMap<String, Tag<?>>();
            }
        }
        if (initial != null) {
            for (Tag t : initial) {
                put(t);
            }
        }
    }

    /**
     * Puts a tag in this map, getting the string from the tag
     *
     * @param tag to add
     * @return the previous value
     */
    public Tag<?> put(Tag<?> tag) {
        return map.put(tag.getName(), tag);
    }

    @Override
    public void clear() {
        map.clear();
    }

    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return map.containsValue(value);
    }

    @Override
    public Set<Map.Entry<String, Tag<?>>> entrySet() {
        return map.entrySet();
    }

    @Override
    public Tag<?> get(Object key) {
        return map.get(key);
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    public Set<String> keySet() {
        return map.keySet();
    }

    @Override
    public Tag<?> put(String key, Tag<?> value) {
        return map.put(key, value);
    }

    @Override
    public void putAll(Map<? extends String, ? extends Tag<?>> values) {
        map.putAll(values);
    }

    @Override
    public Tag remove(Object key) {
        return map.remove(key);
    }

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public Collection<Tag<?>> values() {
        return map.values();
    }

    @Override
    public Iterator<Tag<?>> iterator() {
        return values().iterator();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof CompoundMap) {
            CompoundMap other = (CompoundMap) o;
            Iterator<Tag<?>> iThis = iterator();
            Iterator<Tag<?>> iOther = other.iterator();
            while (iThis.hasNext() && iOther.hasNext()) {
                Tag tThis = iThis.next();
                Tag tOther = iOther.next();
                if (!tThis.equals(tOther)) {
                    return false;
                }
            }
            if (iThis.hasNext() || iOther.hasNext()) {
                return false;
            }
            return true;
        } else {
            return false;
        }
    }
}
