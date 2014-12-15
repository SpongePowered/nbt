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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.flowpowered.nbt.CompoundMap;
import com.flowpowered.nbt.CompoundTag;
import com.flowpowered.nbt.Tag;
import com.flowpowered.nbt.stream.NBTInputStream;
import com.flowpowered.nbt.stream.NBTOutputStream;

/**
 * Holder class for {@link FieldValue FieldValues}
 */
public abstract class FieldHolder {
    private final List<FieldValue<?>> fields = new ArrayList<FieldValue<?>>();

    protected FieldHolder(FieldValue<?>... fields) {
        addFields(fields);
    }

    protected void addFields(FieldValue<?>... fields) {
        Collections.addAll(this.fields, fields);
    }

    public CompoundMap save() {
        CompoundMap map = new CompoundMap();
        for (FieldValue<?> field : fields) {
            field.save(map);
        }
        return map;
    }

    public void load(CompoundTag tag) {
        for (FieldValue<?> field : fields) {
            field.load(tag);
        }
    }

    public void save(File file, boolean compressed) throws IOException {
        save(new FileOutputStream(file), compressed);
    }

    public void save(OutputStream stream, boolean compressed) throws IOException {
        NBTOutputStream os = new NBTOutputStream(stream, compressed);
        os.writeTag(new CompoundTag("", save()));
    }

    public void load(File file, boolean compressed) throws IOException {
        load(new FileInputStream(file), compressed);
    }

    public void load(InputStream stream, boolean compressed) throws IOException {
        NBTInputStream is = new NBTInputStream(stream, compressed);
        Tag<?> tag = is.readTag();
        if (!(tag instanceof CompoundTag)) {
            throw new IllegalArgumentException("Expected CompoundTag, got " + tag.getClass());
        }

        CompoundTag compound = (CompoundTag) tag;
        load(compound);
    }
}
