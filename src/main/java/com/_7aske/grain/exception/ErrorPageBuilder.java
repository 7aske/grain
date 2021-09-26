package com._7aske.grain.exception;

import com._7aske.grain.exception.http.HttpException;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class ErrorPageBuilder {
	private final HttpException exception;

	public ErrorPageBuilder(HttpException exception) {
		this.exception = exception;
	}

	public static String getDefaultErrorPage(Throwable exception, String path) {
		return new ErrorPageBuilder(new HttpException.InternalServerError(exception, path)).build();
	}

	public String build() {
		StringBuilder builder = new StringBuilder();
		builder.append("<html>");
		builder.append("<head>");
		builder.append("<meta charset='utf8'>");
		builder.append(getStyle());
		builder.append("<title>").append(getTitle()).append("</title>");
		builder.append("</head>");
		builder.append("<body>");
		builder.append("<h1>").append(getTitle()).append("</h1>");
		builder.append("<hr>");
		if (exception.getPath() != null)
			builder.append("<p>").append(getRequestPath()).append("</p>");
		builder.append(getDescription());
		builder.append("<hr>");
		if (exception instanceof HttpException.InternalServerError) {
			builder.append("<pre>");
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			PrintStream stream = new PrintStream(bos);
			exception.printStackTrace(stream);
			builder.append(bos);
			builder.append("</pre>");
		}
		builder.append(getStatusBar());
		builder.append("</body>");
		builder.append("</html>");
		return builder.toString();
	}

	private String getStatusBar() {
		return "<h3>Grain</h3>";
	}

	private String getDescription() {
		return String.format("<p><b>Description </b>%s</p>", exception.getMessage() == null ? exception.getStatus().getReason() : exception.getMessage());
	}

	private String getTitle() {
		return String.format("HTTP Status %d - %s", exception.getStatus().getValue(), exception.getStatus().getReason());
	}

	private String getRequestPath() {
		return String.format("<b>Path </b> %s", exception.getPath());
	}

	private static String getStyle() {
		return "<style type=\"text/css\">body {font-family:Tahoma,Arial,sans-serif;} h1, h2, h3, b {color:white;background-color:#525D76;} b {margin-right:.25em} h1 {font-size:22px;} h2 {font-size:16px;} h3 {font-size:14px;} p {font-size:12px;} a {color:black;} .line {height:1px;background-color:#525D76;border:none;}</style>";
	}
}
