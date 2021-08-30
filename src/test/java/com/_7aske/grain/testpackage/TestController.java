package com._7aske.grain.testpackage;

import com._7aske.grain.component.Controller;
import com._7aske.grain.controller.RequestMapping;
import com._7aske.grain.http.view.View;

@Controller
@RequestMapping
public class TestController {

	@RequestMapping
	public View get() {
		return new View("test.html");
	}

	@RequestMapping("static/style.css")
	public View style() {
		return new View("static/style.css");
	}
}
