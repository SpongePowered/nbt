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

import java.nio.charset.Charset;

/**
 * A class which holds constant values.
 */
public final class NBTConstants {
    /**
     * The character set used by NBT (UTF-8).
     */
    public static final Charset CHARSET = Charset.forName("UTF-8");
    /**
     * Tag type constants.
     */
    @Deprecated
    public static final int TYPE_END = TagType.TAG_END.getId(),
            TYPE_BYTE = TagType.TAG_BYTE.getId(),
            TYPE_SHORT = TagType.TAG_SHORT.getId(),
            TYPE_INT = TagType.TAG_INT.getId(),
            TYPE_LONG = TagType.TAG_LONG.getId(),
            TYPE_FLOAT = TagType.TAG_FLOAT.getId(),
            TYPE_DOUBLE = TagType.TAG_DOUBLE.getId(),
            TYPE_BYTE_ARRAY = TagType.TAG_BYTE_ARRAY.getId(),
            TYPE_STRING = TagType.TAG_STRING.getId(),
            TYPE_LIST = TagType.TAG_LIST.getId(),
            TYPE_COMPOUND = TagType.TAG_COMPOUND.getId(),
            TYPE_INT_ARRAY = TagType.TAG_INT_ARRAY.getId(),
            TYPE_SHORT_ARRAY = TagType.TAG_SHORT_ARRAY.getId();

    /**
     * Default private constructor.
     */
    private NBTConstants() {
    }
}
