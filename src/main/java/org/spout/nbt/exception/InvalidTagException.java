package org.spout.nbt.exception;

import org.spout.nbt.Tag;

public class InvalidTagException extends Exception {
	public InvalidTagException(Tag t) {
		System.out.println("Invalid tag: " + t.toString() + " encountered!");
	}
}
