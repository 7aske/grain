package com._7aske.grain.orm.model;

import com._7aske.grain.orm.annotation.Column;
import com._7aske.grain.orm.annotation.Id;
import com._7aske.grain.orm.annotation.Table;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ModelTest {
	@Table(name = "test")
	public static final class TestModel extends Model {
		@Id
		@Column(name = "test_id")
		private Integer id;

		@Column(name = "name")
		private String name;
	}

	@Test
	void testModel_instantiates() {
		TestModel testModel = new TestModel();
	}

	@Test
	void testModel_hasRequiredFields() {
		TestModel testModel = new TestModel();

		assertEquals("test", testModel.getTable().name());
		assertFalse(testModel.getFields().isEmpty());
		assertFalse(testModel.getIds().isEmpty());
	}
}