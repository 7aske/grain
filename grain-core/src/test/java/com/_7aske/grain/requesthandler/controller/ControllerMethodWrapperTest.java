package com._7aske.grain.requesthandler.controller;

import com._7aske.grain.exception.GrainRuntimeException;
import com._7aske.grain.web.controller.annotation.GetMapping;
import com._7aske.grain.web.controller.annotation.PathVariable;
import com._7aske.grain.web.controller.annotation.RequestMapping;
import com._7aske.grain.web.http.HttpMethod;
import com._7aske.grain.web.requesthandler.controller.wrapper.ControllerMethodWrapper;
import com._7aske.grain.web.requesthandler.controller.wrapper.ControllerWrapper;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ControllerMethodWrapperTest {
	@RequestMapping
	static final class TestController {
		@GetMapping("/test/{id}/name/{name}")
		public void testEndpoint(@PathVariable("id") String test) {
			System.out.println(test);
		}
	}

	@RequestMapping
	static final class TestControllerWithLessVariables {
		@GetMapping("/test/{id}")
		public void testEndpoint(@PathVariable("id") String test, @PathVariable("name") String name) {
			System.out.println(test);
		}
	}

	@Test
	void test_pathParsing() {
		ControllerWrapper wrapper = new ControllerWrapper(new TestController());
		ControllerMethodWrapper methodWrapper = wrapper.getMethods().get(0);
		assertEquals("/test/{id}/name/{name}", methodWrapper.getPath());
		assertEquals(List.of(HttpMethod.GET), methodWrapper.getHttpMethods());
	}

	@Test
	void test_throwsOnMoreDeclaredThanDefinedVariables() {
		assertThrows(GrainRuntimeException.class, () -> {
			new ControllerWrapper(new TestControllerWithLessVariables());
		});
	}
}