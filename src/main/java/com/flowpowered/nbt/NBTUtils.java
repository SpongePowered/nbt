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

/**
 * A class which contains NBT-related utility methods.
 */
public final class NBTUtils {
    /**
     * Gets the type name of a tag.
     *
     * @param clazz The tag class.
     * @return The type name.
     */
    @Deprecated
    public static String getTypeName(Class<? extends Tag<?>> clazz) {
        return TagType.getByTagClass(clazz).getTypeName();
    }

    /**
     * Gets the type code of a tag class.
     *
     * @param clazz The tag class.
     * @return The type code.
     * @throws IllegalArgumentException if the tag class is invalid.
     */
    @Deprecated
    public static int getTypeCode(Class<? extends Tag<?>> clazz) {
        return TagType.getByTagClass(clazz).getId();
    }

    /**
     * Gets the class of a type of tag.
     *
     * @param type The type.
     * @return The class.
     * @throws IllegalArgumentException if the tag type is invalid.
     */
    @Deprecated
    public static Class<? extends Tag> getTypeClass(int type) {
        return TagType.getById(type).getTagClass();
    }

    /**
     * Default private constructor.
     */
    private NBTUtils() {
    }
}
