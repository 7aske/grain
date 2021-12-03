package com._7aske.grain.util;

import com._7aske.grain.GrainApp;
import com._7aske.grain.component.Controller;
import com._7aske.grain.component.Grain;
import org.junit.jupiter.api.Test;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.sql.Ref;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ReflectionUtilTest {
	@Test
	void testCompareLibraryAndUserPackage() {
		LinkedList<String> pkgs = new LinkedList<>(List.of(
				"com._7aske.grain.entity.user",
				"com._7aske.graintestapp.controller.test",
				"com._7aske.grain.controller.test",
				"com.google.controller.test",
				"com.tomislavzivadinovic.backend.controller.UserController",
				"xyz.todooc4.backend.controller.UserController",
				"com._7aske.grain.util.whatever"
		));
		pkgs.sort(ReflectionUtil::compareLibraryAndUserPackage);
		assertFalse(pkgs.getFirst().startsWith(GrainApp.class.getPackageName() + "."));
		assertTrue(pkgs.getLast().startsWith(GrainApp.class.getPackageName() + "."));
	}

	@Inherited
	@Controller
	@Retention(RetentionPolicy.RUNTIME)
	@interface CompositeAnnotation {

	}

	@Controller // has @Grain
	static class TestClass {

	}

	@CompositeAnnotation
	static class TestCompositeClass {

	}

	@Test
	void testIsAnnotationPresent() {
		assertTrue(ReflectionUtil.isAnnotationPresent(TestClass.class, Grain.class));
		assertTrue(ReflectionUtil.isAnnotationPresent(TestCompositeClass.class, Grain.class));
	}
}