package com._7aske.grain.web.ui.impl;

import com._7aske.grain.annotation.NotNull;
import com._7aske.grain.core.component.ConditionalOnMissingGrain;
import com._7aske.grain.core.component.Grain;
import com._7aske.grain.web.http.ContentType;
import com._7aske.grain.web.ui.LoginPage;
import com._7aske.grain.web.ui.util.Styles;

@Grain
@ConditionalOnMissingGrain(LoginPage.class)
public class DefaultLoginPage implements LoginPage {
	public @NotNull String getContent() {
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
				"        <input name=\"username\" type=\"text\" placeholder=\"Username\" autoFocus=\"autoFocus\"/>\n" +
				"        <input name=\"password\" type=\"password\" placeholder=\"Password\"/>\n" +
				"        <button>Login</button>\n" +
				"        <div style=\"display: none\" class=\"alert error\">Invalid credentials</div>\n" +
				"        <div style=\"display: none;\" class=\"alert logout\">Successfully logged out</div>\n" +
				"      </form>\n" +
				"    </div>\n" +
				"  </div>\n" +
				"<script>\n" +
				"    const params = new URLSearchParams(window.location.search);\n" +
				"    if (params.has(\"error\")) {\n" +
				"        document.querySelector(\".alert.error\").style.display = \"block\";\n" +
				"    }\n" +
				"    if (params.has(\"logout\")) {\n" +
				"        document.querySelector(\".alert.logout\").style.display = \"block\";\n" +
				"    }\n" +
				"</script>\n" +
				"</body>\n" +
				"</html>";
	}

	@NotNull
	@Override
	public String getName() {
		return "login.gtl";
	}

	@Override
	public @NotNull String getContentType() {
		return ContentType.TEXT_HTML;
	}
}
