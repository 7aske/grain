package com._7aske.grain.testpackage;

import com._7aske.grain.component.Controller;
import com._7aske.grain.controller.annotation.RequestMapping;
import com._7aske.grain.http.HttpMethod;
import com._7aske.grain.http.HttpRequest;
import com._7aske.grain.http.json.JsonBody;
import com._7aske.grain.http.view.TemplateView;

@Controller
@RequestMapping
public class TestController {

	@RequestMapping("test")
	public TemplateView get() {
		String firstName = "John";
		String lastName = "Smith";
		TemplateView templateView = new TemplateView("index.html");
		templateView.setData("firstName", firstName);
		templateView.setData("lastName", lastName);
		return templateView;
	}

	@RequestMapping(value = "json", method = HttpMethod.POST)
	public String postJson(HttpRequest request) {
		return request.getBody().toString();
	}

	static class User {
		String username;
		String password;

	}

	@RequestMapping(value = "login", method = HttpMethod.POST)
	public String postJson(@JsonBody User user) {
		return String.format("%s logged in", user.username);
	}
}
