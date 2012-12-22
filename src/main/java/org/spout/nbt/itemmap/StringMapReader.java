package org.spout.nbt.itemmap;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.spout.nbt.IntTag;
import org.spout.nbt.Tag;

public class StringMapReader {
	
	public static List<Tag<?>> readFile(File f) {
		
		List<Tag<?>> list = new ArrayList<Tag<?>>();
		
		try {
			FileInputStream fis = new FileInputStream(f);
			DataInputStream dis = new DataInputStream(fis);
			boolean eof = false;
			while (!eof) {
				int value;
				String key;
				try {
					value = dis.readInt();
				} catch (EOFException e) {
					eof = true;
					continue;
				}
				key = dis.readUTF();
				list.add(new IntTag(key, value));
				
			}
			return list;
		} catch (IOException ioe) {
			return null;
		}
	}

}
