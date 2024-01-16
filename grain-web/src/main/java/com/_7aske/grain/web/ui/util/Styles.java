package com._7aske.grain.web.ui.util;

public class Styles {
	private Styles() {}

	private static final String COMMON_STYLES = """
				@import url(https://fonts.googleapis.com/css?family=Roboto:300);
				* {
				  box-sizing:border-box;
				}
				html, body {
				  min-height: 100vh;
				  margin:0;
				  padding:0;
				}
				header .header {
				  background-color: #fff;
				  height: 45px;
				}
				    
				header a img {
				  width: 134px;
				  margin-top: 4px;
				}
				    
				.login-page {
				  width: 360px;
				  padding: 8% 0 0;
				  margin: auto;
				}
				    
				.login-page .form .login {
				  margin-top: -31px;
				  margin-bottom: 26px;
				}
				    
				.error-page {
				  padding: 8% 0 0;
				  margin: auto;
				}
				    
				.error-page .form {
				  max-width: 1024px;
				  text-align: center;
				}
				    
				.error-page .form pre {
				  text-align: left;
				  overflow-x: scroll;
				  font-size: 12px;
				}
				    
				.login-page .form {
				  max-width: 360px;
				  text-align: center;
				}
				    
				.form {
				  position: relative;
				  z-index: 1;
				  background: #ffffff;
				  margin: 0 auto 100px;
				  padding: 45px;
				  box-shadow: 0 0 20px 0 rgba(0, 0, 0, 0.2), 0 5px 5px 0 rgba(0, 0, 0, 0.24);
				}
				    
				.form input {
				  font-family: "Roboto", sans-serif;
				  outline: 0;
				  background: #f2f2f2;
				  width: 100%;
				  border: 0;
				  margin: 0 0 15px;
				  padding: 15px;
				  box-sizing: border-box;
				  font-size: 14px;
				}
				    
				.form button {
				  font-family: "Roboto", sans-serif;
				  text-transform: uppercase;
				  outline: 0;
				  background-color: #328f8a;
				  background-image: linear-gradient(45deg, #328f8a, #08ac4b);
				  width: 100%;
				  border: 0;
				  padding: 15px;
				  color: #ffffff;
				  font-size: 14px;
				  -webkit-transition: all 0.3 ease;
				  transition: all 0.3 ease;
				  cursor: pointer;
				}
				    
				.container {
				  position: relative;
				  z-index: 1;
				  margin: 0 auto;
				}
				    
				body {
				  background-color: #328f8a;
				  background-image: linear-gradient(45deg, #328f8a, #08ac4b);
				  font-family: "Roboto", sans-serif;
				  -webkit-font-smoothing: antialiased;
				  -moz-osx-font-smoothing: grayscale;
				}
				.alert {
				   font-family: "Roboto", sans-serif;
				   margin-top: 0.25em;
				   padding: 15px;
				   color: white;
				   outline: 0;
				   border: 0;
				   font-size: 14px;
				   -webkit-transition: all 0.3 ease;
				   transition: all 0.3 ease;
				}
				.alert.error {
				   background-image: linear-gradient(45deg, #FF7C7C, #EB4747);
				}
				.alert.logout {
				   background-image: linear-gradient(45deg, #FFEF82, #EFD345);
				   color: #545454;
				}
				    
				""".stripIndent();

	public static String getCommonStyles() {
		return COMMON_STYLES;
	}
}
