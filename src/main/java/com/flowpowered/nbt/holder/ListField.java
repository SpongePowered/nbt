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

import java.util.ArrayList;
import java.util.List;

import com.flowpowered.nbt.ListTag;
import com.flowpowered.nbt.Tag;

/**
 * Represents a field that contains a list of other tags (all tags are of the same type)
 */
public class ListField<T> implements Field<List<T>> {
    private final Field<T> backingField;

    public ListField(Field<T> field) {
        this.backingField = field;
    }

    public List<T> getValue(Tag<?> tag) throws IllegalArgumentException {
        ListTag<?> listTag = FieldUtils.checkTagCast(tag, ListTag.class);
        List<T> result = new ArrayList<T>();
        for (Tag<?> element : listTag.getValue()) {
            result.add(backingField.getValue(element));
        }
        return result;
    }

    @SuppressWarnings ("unchecked")
    public Tag<?> getValue(String name, List<T> value) {
        List<Tag<?>> tags = new ArrayList<Tag<?>>();
        Class tagClazz = Tag.class; // Generics suck (I had to move this comment 3 times while finding the right place to nuke generics too)
        for (T element : value) {
            Tag<?> tag = backingField.getValue("", element);
            tagClazz = tag.getClass();
            tags.add(tag);
        }

        return new ListTag<Tag<?>>(name, tagClazz, tags);
    }
}
