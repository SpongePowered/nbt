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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import com.flowpowered.nbt.CompoundTag;
import com.flowpowered.nbt.Tag;

/**
 * A field that holds the contents of a FieldHolder
 */
public class FieldHolderField<T extends FieldHolder> implements Field<T> {
    private final Class<T> type;
    private final Constructor<T> typeConst;

    public FieldHolderField(Class<T> type) {
        this.type = type;
        try {
            typeConst = type.getConstructor();
            typeConst.setAccessible(true);
        } catch (NoSuchMethodException e) {
            throw new ExceptionInInitializerError("Type must have zero-arg constructor!");
        }
    }

    public T getValue(Tag<?> rawTag) throws IllegalArgumentException {
        CompoundTag tag = FieldUtils.checkTagCast(rawTag, CompoundTag.class);

        T value = null;
        try {
            value = typeConst.newInstance();
            value.load(tag);
        } catch (InstantiationException e) {
        } catch (IllegalAccessException e) {
        } catch (InvocationTargetException e) {
        }
        return value;
    }

    public Tag<?> getValue(String name, T value) {
        return new CompoundTag(name, value.save());
    }
}
