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
import java.util.HashMap;
import java.util.Map;

import com.flowpowered.nbt.ByteTag;
import com.flowpowered.nbt.Tag;

/**
 * Represents a field containing a basic tag type
 */
public class BasicTagField<T> implements Field<T> {
    private static final Map<Class<? extends Tag<?>>, Constructor<Tag<?>>> CONSTRUCTOR_CACHE = new HashMap<Class<? extends Tag<?>>, Constructor<Tag<?>>>();

    static {
        try {
            //noinspection unchecked
            CONSTRUCTOR_CACHE.put(ByteTag.class, (Constructor) ByteTag.class.getConstructor(String.class, byte.class)); // ByteTag has a constructor that takes a boolean too, we don't want to use that
        } catch (NoSuchMethodException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private final Class<? extends Tag<T>> valueType;

    public BasicTagField(Class<? extends Tag<T>> valueType) {
        this.valueType = valueType;
    }

    public T getValue(Tag<?> tag) throws IllegalArgumentException {
        Tag<T> value = FieldUtils.checkTagCast(tag, valueType);
        return value.getValue();
    }

    public Tag<T> getValue(String name, T value) {
        Constructor<Tag<T>> constr = getConstructor(valueType);
        constr.setAccessible(true);
        try {
            return constr.newInstance(name, value);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException ignore) { // Should not happen, we set accessible to true
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @SuppressWarnings ("unchecked")
    private static <T> Constructor<Tag<T>> getConstructor(Class<? extends Tag<T>> tag) {
        // WARNING: Java generics suck, ugly code ahead
        Constructor<Tag<T>> constructor = (Constructor) CONSTRUCTOR_CACHE.get(tag);
        if (constructor == null) {
            Constructor<?>[] constructors = tag.getConstructors();
            if (constructors.length == 1
                    && constructors[0].getParameterTypes().length == 2
                    && String.class.isAssignableFrom(constructors[0].getParameterTypes()[0])) {
                constructor = (Constructor<Tag<T>>) constructors[0];
                CONSTRUCTOR_CACHE.put(tag, (Constructor) constructor);
            } else {
                throw new IllegalArgumentException(tag + " does not have one constructor with the correct type!");
            }
        }
        return constructor;
    }
}
