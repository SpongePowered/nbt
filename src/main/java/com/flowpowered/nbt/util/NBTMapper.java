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
package com.flowpowered.nbt.util;

import com.flowpowered.nbt.Tag;

/**
 * Utility class to map out complex objects into NBT structures vice-versa.
 */
public class NBTMapper {
    /**
     * Takes in an NBT tag, sanely checks null status, and then returns its value.
     *
     * @param t Tag to get value from
     * @return tag value as an object or null if no value
     */
    public static Object toTagValue(Tag<?> t) {
        if (t == null) {
            return null;
        } else {
            return t.getValue();
        }
    }

    /**
     * Takes in an NBT tag, sanely checks null status, and then returns it value. This method will return null if the value cannot be cast to the given class.
     *
     * @param t Tag to get the value from
     * @param clazz the return type to use
     * @return the value as an onbject of the same type as the given class
     */
    public static <T> T getTagValue(Tag<?> t, Class<? extends T> clazz) {
        Object o = toTagValue(t);
        if (o == null) {
            return null;
        }
        try {
            return clazz.cast(o);
        } catch (ClassCastException e) {
            return null;
        }
    }

    /**
     * Takes in an NBT tag, sanely checks null status, and then returns it value. This method will return null if the value cannot be cast to the default value.
     *
     * @param t Tag to get the value from
     * @param defaultValue the value to return if the tag or its value is null or the value cannot be cast
     * @return the value as an onbject of the same type as the default value, or the default value
     */
    public static <T, U extends T> T toTagValue(Tag<?> t, Class<? extends T> clazz, U defaultValue) {
        Object o = toTagValue(t);
        if (o == null) {
            return defaultValue;
        }
        try {
            T value = clazz.cast(o);
            return value;
        } catch (ClassCastException e) {
            return defaultValue;
        }
    }
}
