package com._7aske.grain.testpackage;

import com._7aske.grain.component.Controller;
import com._7aske.grain.controller.RequestMapping;
import com._7aske.grain.http.view.DataView;

@Controller
@RequestMapping
public class TestController {

	@RequestMapping("test")
	public DataView get() {
		String firstName = "John";
		String lastName = "Smith";
		DataView dataView = new DataView("index.html");
		dataView.setData("firstName", firstName);
		dataView.setData("lastName", lastName);
		return dataView;
	}
}
