package com._7aske.grain.web.util;

import com._7aske.grain.GrainApp;
import com._7aske.grain.web.controller.annotation.Controller;
import com._7aske.grain.core.component.Grain;
import com._7aske.grain.core.reflect.ReflectionUtil;
import com._7aske.grain.util.By;
import com._7aske.grain.web.controller.annotation.RequestMapping;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.LinkedList;
import java.util.List;

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
		pkgs.sort(By::packages);
		Assertions.assertFalse(pkgs.getFirst().startsWith(GrainApp.class.getPackageName() + "."));
		Assertions.assertTrue(pkgs.getLast().startsWith(GrainApp.class.getPackageName() + "."));
	}

	@Inherited
	@Controller
	@Retention(RetentionPolicy.RUNTIME)
	@interface CompositeAnnotation {

	}

	@Controller // has @Grain
	@RequestMapping
	static class TestClass {

	}

	@CompositeAnnotation
	@RequestMapping
	static class TestCompositeClass {

	}

	@Test
	void testIsAnnotationPresent() {
		Assertions.assertTrue(ReflectionUtil.isAnnotationPresent(TestClass.class, Grain.class));
		Assertions.assertTrue(ReflectionUtil.isAnnotationPresent(TestCompositeClass.class, Grain.class));
	}
}