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

import java.util.Arrays;

public class ShortArrayTag extends Tag<short[]> {
    /**
     * The value.
     */
    private final short[] value;

    /**
     * Creates the tag.
     *
     * @param name The name.
     * @param value The value.
     */
    public ShortArrayTag(String name, short[] value) {
        super(TagType.TAG_SHORT_ARRAY, name);
        this.value = value;
    }

    @Override
    public short[] getValue() {
        return value;
    }

    @Override
    public String toString() {
        StringBuilder hex = new StringBuilder();
        for (short s : value) {
            String hexDigits = Integer.toHexString(s).toUpperCase();
            switch (hexDigits.length()) {
                case 8:
                case 7:
                case 6:
                case 5:
                    hexDigits = hexDigits.substring(hexDigits.length() - 4);
                    break;
                case 1:
                    hex.append("0");
                case 2:
                    hex.append("0");
                case 3:
                    hex.append("0");
            }
            hex.append(hexDigits).append(" ");
        }

        String name = getName();
        String append = "";
        if (name != null && !name.equals("")) {
            append = "(\"" + this.getName() + "\")";
        }
        return "TAG_Short_Array" + append + ": " + hex.toString();
    }

    public ShortArrayTag clone() {
        short[] clonedArray = cloneArray(value);

        return new ShortArrayTag(getName(), clonedArray);
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof ShortArrayTag)) {
            return false;
        }

        ShortArrayTag tag = (ShortArrayTag) other;
        return Arrays.equals(value, tag.value) && getName().equals(tag.getName());
    }

    private short[] cloneArray(short[] shortArray) {
        if (shortArray == null) {
            return null;
        } else {
            int length = shortArray.length;
            short[] newArray = new short[length];
            System.arraycopy(shortArray, 0, newArray, 0, length);
            return newArray;
        }
    }
}
