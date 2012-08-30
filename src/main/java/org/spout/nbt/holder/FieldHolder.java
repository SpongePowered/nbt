package org.spout.nbt.holder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.spout.nbt.CompoundMap;
import org.spout.nbt.CompoundTag;
import org.spout.nbt.Tag;
import org.spout.nbt.stream.NBTInputStream;
import org.spout.nbt.stream.NBTOutputStream;

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
