package org.spout.nbt.holder;

import org.junit.Before;
import org.junit.Test;
import org.spout.nbt.CompoundMap;
import org.spout.nbt.CompoundTag;
import org.spout.nbt.StringTag;

import static org.junit.Assert.*;

/**
 * Test for {@link FieldHolder}
 */
public class FieldHolderTest {
	private ExampleHolder subject;

	@Before
	public void setUp() {
		subject = new ExampleHolder();
		CompoundMap map = new CompoundMap();
		map.put(new StringTag("name", "helloworld"));
		CompoundTag mapTag = new CompoundTag("", map);
		subject.load(mapTag);
	}

	private static class ExampleHolder extends FieldHolder {
		public final FieldValue<String> name = FieldValue.from("name", new BasicTagField<String>(StringTag.class)),
		unassignedDefault = FieldValue.from("unassigned", new BasicTagField<String>(StringTag.class), "value-here");

		public ExampleHolder() {
			addFields(name, unassignedDefault);
		}
	}

	@Test
	public void testBasicLoad() {
		assertEquals("helloworld", subject.name.get());
		assertEquals("value-here", subject.unassignedDefault.get());
	}

	@Test
	public void testDefaultValue() {
		assertEquals("value-here", subject.unassignedDefault.get());
	}
}
