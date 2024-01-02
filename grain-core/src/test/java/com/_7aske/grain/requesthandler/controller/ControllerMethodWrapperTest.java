package com._7aske.grain.requesthandler.controller;

import com._7aske.grain.core.component.Controller;
import com._7aske.grain.web.requesthandler.controller.wrapper.ControllerMethodWrapper;
import com._7aske.grain.web.requesthandler.controller.wrapper.ControllerWrapper;
import com._7aske.grain.util.ReflectionUtil;
import com._7aske.grain.web.controller.annotation.PathVariable;
import com._7aske.grain.web.controller.annotation.RequestMapping;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ControllerMethodWrapperTest {
	@Controller
	@RequestMapping
	static final class TestController {
		@RequestMapping("/test/{id}/name/{name}")
		public void testEndpoint(@PathVariable("id") String test) {
			System.out.println(test);
		}
	}

	@Test
	void testPathVariableParsing() {
		ControllerWrapper wrapper = new ControllerWrapper(new TestController());
		ControllerMethodWrapper methodWrapper = wrapper.getMethods().get(0);
		List<String> pathVariables = (List<String>) ReflectionUtil.getFieldValue(methodWrapper, "pathVariables");
		assertEquals("id", pathVariables.get(0));
		assertEquals("name", pathVariables.get(1));
	}
}