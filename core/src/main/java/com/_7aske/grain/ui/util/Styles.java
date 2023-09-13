package com._7aske.grain.ui.util;

public class Styles {
	private Styles() {}

	public static String getCommonStyles() {
		return "@import url(https://fonts.googleapis.com/css?family=Roboto:300);\n" +
				"html {\n" +
				"  min-height: 100vh;\n" +
				"}\n" +
				"header .header {\n" +
				"  background-color: #fff;\n" +
				"  height: 45px;\n" +
				"}\n" +
				"\n" +
				"header a img {\n" +
				"  width: 134px;\n" +
				"  margin-top: 4px;\n" +
				"}\n" +
				"\n" +
				".login-page {\n" +
				"  width: 360px;\n" +
				"  padding: 8% 0 0;\n" +
				"  margin: auto;\n" +
				"}\n" +
				"\n" +
				".login-page .form .login {\n" +
				"  margin-top: -31px;\n" +
				"  margin-bottom: 26px;\n" +
				"}\n" +
				"\n" +
				".error-page {\n" +
				"  padding: 8% 0 0;\n" +
				"  margin: auto;\n" +
				"}\n" +
				"\n" +
				".error-page .form {\n" +
				"  max-width: 1024px;\n" +
				"  text-align: center;\n" +
				"}\n" +
				"\n" +
				".error-page .form pre {\n" +
				"  text-align: left;\n" +
				"  overflow-x: scroll;\n" +
				"  font-size: 12px;\n" +
				"}\n" +
				"\n" +
				".login-page .form {\n" +
				"  max-width: 360px;\n" +
				"  text-align: center;\n" +
				"}\n" +
				"\n" +
				".form {\n" +
				"  position: relative;\n" +
				"  z-index: 1;\n" +
				"  background: #ffffff;\n" +
				"  margin: 0 auto 100px;\n" +
				"  padding: 45px;\n" +
				"  box-shadow: 0 0 20px 0 rgba(0, 0, 0, 0.2), 0 5px 5px 0 rgba(0, 0, 0, 0.24);\n" +
				"}\n" +
				"\n" +
				".form input {\n" +
				"  font-family: \"Roboto\", sans-serif;\n" +
				"  outline: 0;\n" +
				"  background: #f2f2f2;\n" +
				"  width: 100%;\n" +
				"  border: 0;\n" +
				"  margin: 0 0 15px;\n" +
				"  padding: 15px;\n" +
				"  box-sizing: border-box;\n" +
				"  font-size: 14px;\n" +
				"}\n" +
				"\n" +
				".form button {\n" +
				"  font-family: \"Roboto\", sans-serif;\n" +
				"  text-transform: uppercase;\n" +
				"  outline: 0;\n" +
				"  background-color: #328f8a;\n" +
				"  background-image: linear-gradient(45deg, #328f8a, #08ac4b);\n" +
				"  width: 100%;\n" +
				"  border: 0;\n" +
				"  padding: 15px;\n" +
				"  color: #ffffff;\n" +
				"  font-size: 14px;\n" +
				"  -webkit-transition: all 0.3 ease;\n" +
				"  transition: all 0.3 ease;\n" +
				"  cursor: pointer;\n" +
				"}\n" +
				"\n" +
				".container {\n" +
				"  position: relative;\n" +
				"  z-index: 1;\n" +
				"  margin: 0 auto;\n" +
				"}\n" +
				"\n" +
				"body {\n" +
				"  background-color: #328f8a;\n" +
				"  background-image: linear-gradient(45deg, #328f8a, #08ac4b);\n" +
				"  font-family: \"Roboto\", sans-serif;\n" +
				"  -webkit-font-smoothing: antialiased;\n" +
				"  -moz-osx-font-smoothing: grayscale;\n" +
				"}\n"+
				".alert {\n" +
				"   font-family: \"Roboto\", sans-serif;\n" +
				"   margin-top: 0.25em;\n" +
				"   padding: 15px;\n" +
				"   color: white;\n" +
				"   outline: 0;\n" +
				"   border: 0;\n" +
				"   font-size: 14px;\n" +
				"   -webkit-transition: all 0.3 ease;\n" +
				"   transition: all 0.3 ease;\n" +
				"}\n" +
				".alert.error {\n" +
				"   background-image: linear-gradient(45deg, #FF7C7C, #EB4747);\n" +
				"}\n" +
				".alert.logout {\n" +
				"   background-image: linear-gradient(45deg, #FFEF82, #EFD345);\n" +
				"   color: #545454;\n" +
				"}\n";
	}
}
