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

import com.flowpowered.nbt.CompoundMap;
import com.flowpowered.nbt.CompoundTag;
import com.flowpowered.nbt.Tag;

/**
 * Represents the value of a field
 */
public class FieldValue<T> {
    private T value;
    private final Field<T> field;
    private final String key;
    private final T defaultValue;

    public FieldValue(String key, Field<T> field) {
        this(key, field, null);
    }

    public FieldValue(String key, Field<T> field, T defaultValue) {
        this.field = field;
        this.key = key;
        this.defaultValue = defaultValue;
    }

    /**
     * Get this field from a CompoundTag
     *
     * @param tag The tag to get this field from
     * @return The value
     */
    public T load(CompoundTag tag) {
        Tag subTag = tag.getValue().get(key);
        if (subTag == null) {
            return (value = defaultValue);
        }
        return (value = field.getValue(subTag));
    }

    public void save(CompoundMap tag) {
        T value = this.value;
        if (value == null) {
            if ((value = defaultValue) == null) {
                return;
            }
        }
        Tag t = field.getValue(key, value);
        tag.put(t);
    }

    public T get() {
        return value;
    }

    public void set(T value) {
        this.value = value;
    }

    // So generic info doesn't have to be duplicated
    public static <T> FieldValue<T> from(String name, Field<T> field, T defaultValue) {
        return new FieldValue<T>(name, field, defaultValue);
    }

    public static <T> FieldValue<T> from(String name, Field<T> field) {
        return new FieldValue<T>(name, field);
    }
}
