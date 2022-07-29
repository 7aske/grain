package com._7aske.grain.orm.model;

import com._7aske.grain.ApplicationContextHolder;
import com._7aske.grain.GrainApp;
import com._7aske.grain.config.Configuration;
import com._7aske.grain.core.context.ApplicationContext;
import com._7aske.grain.core.context.ApplicationContextImpl;
import com._7aske.grain.orm.annotation.Column;
import com._7aske.grain.orm.annotation.Id;
import com._7aske.grain.orm.annotation.Table;
import com._7aske.grain.orm.database.DatabaseExecutor;
import com._7aske.grain.orm.exception.GrainDbConnectionException;
import com._7aske.grain.orm.querybuilder.helper.ModelClass;
import com._7aske.grain.requesthandler.staticlocation.StaticLocationsRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static com._7aske.grain.config.ConfigurationKey.*;
import static org.junit.jupiter.api.Assertions.*;

class ModelTest {
	public static final class TestApp extends GrainApp {

	}

	@Table(name = "test")
	public static final class TestModel extends Model {
		@Id
		@Column(name = "test_id")
		private Integer id;

		@Column(name = "name")
		private String name;
	}

	// This we avoid setting up the whole application in order to have application
	// context in the ApplicationContextHolder class.
	@BeforeEach
	void setup() throws NoSuchFieldException, IllegalAccessException {
		ApplicationContextHolder.setContext(null);
		Configuration configuration = Configuration.createDefault();
		configuration.set(DATABASE_HOST, "127.0.0.1");
		configuration.set(DATABASE_PORT, 3306);
		configuration.set(DATABASE_NAME, "test");
		configuration.set("grain.persistence.provider", "native");
		ApplicationContext context = new ApplicationContextImpl(ModelTest.TestApp.class.getPackageName(),
				configuration,
				StaticLocationsRegistry.createDefault());
		Field field = ApplicationContextHolder.class.getDeclaredField("applicationContext");
		field.setAccessible(true);
		field.set(null, context);
	}

	@Test
	void testModel_instantiates() {
		assertDoesNotThrow(TestModel::new);
	}

	@Test
	void testModel_hasRequiredFields() {
		TestModel testModel = new TestModel();
		ModelClass modelClass = new ModelClass(testModel.getClass());

		assertEquals("test", modelClass.getTableName());
		assertFalse(modelClass.getColumnFields().isEmpty());
		assertFalse(modelClass.getIdFields().isEmpty());
	}

	@Test
	void testModel_hasDatabaseExecutor() {
		TestModel testModel = new TestModel();
		DatabaseExecutor executor = testModel.getDatabaseExecutor();
		assertNotNull(executor);
	}

	@Test
	void testModel_methodInvocation() {
		TestModel testModel = new TestModel();
		// @Note should throw because in development we don't use any of the
		// driver classes.
		assertThrows(GrainDbConnectionException.class, testModel::save);
	}
}