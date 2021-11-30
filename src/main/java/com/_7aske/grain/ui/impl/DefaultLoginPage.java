package com._7aske.grain.ui.impl;

import com._7aske.grain.component.Grain;
import com._7aske.grain.http.HttpContentType;
import com._7aske.grain.ui.LoginPage;
import com._7aske.grain.ui.util.Styles;

@Grain
public class DefaultLoginPage implements LoginPage {
	public String getContent() {
		return "<!DOCTYPE html>\n" +
				"<html>\n" +
				"<head>\n" +
				"<title> Grain | Login </title>\n" +
				"<style>" + Styles.getCommonStyles() + "</style>" +
				"</head>\n" +
				"<body>\n" +
				"  <div class=\"login-page\">\n" +
				"    <div class=\"form\">\n" +
				"      <div class=\"login\">\n" +
				"        <div class=\"login-header\">\n" +
				"          <h3>LOGIN</h3>\n" +
				"          <p>Please enter your credentials to login.</p>\n" +
				"        </div>\n" +
				"      </div>\n" +
				"      <form method=\"post\" action=\"/login\" class=\"login-form\">\n" +
				"        <input name=\"username\" type=\"text\" placeholder=\"Username\"/>\n" +
				"        <input name=\"password\" type=\"password\" placeholder=\"Password\"/>\n" +
				"        <button>Login</button>\n" +
				"      </form>\n" +
				"    </div>\n" +
				"  </div>\n" +
				"</body>\n" +
				"</html>";
	}

	@Override
	public String getContentType() {
		return HttpContentType.TEXT_HTML;
	}
}
