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

import com.flowpowered.nbt.Tag;

/**
 * Utility classes for field handling. Idiot Java doesn't allow concrete static methods in interfaces.
 */
public class FieldUtils {
    private FieldUtils() {
    }

    /**
     * Checks that a tag is not null and of the required type
     *
     * @param tag The tag to check
     * @param type The type of tag required
     * @param <T> The type parameter of {@code type}
     * @return The casted tag
     * @throws IllegalArgumentException if the tag is null or not of the required type
     */
    public static <T extends Tag<?>> T checkTagCast(Tag<?> tag, Class<T> type) throws IllegalArgumentException {
        if (tag == null) {
            throw new IllegalArgumentException("Expected tag of type " + type.getName() + ", was null");
        } else if (!type.isInstance(tag)) {
            throw new IllegalArgumentException("Expected tag to be a " + type.getName() + ", was a " + tag.getClass().getName());
        }
        return type.cast(tag);
    }
}
