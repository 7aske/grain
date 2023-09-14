package com._7aske.grain.ui.impl;

import com._7aske.grain.annotation.NotNull;
import com._7aske.grain.annotation.Nullable;
import com._7aske.grain.exception.http.HttpException;
import com._7aske.grain.http.HttpContentType;
import com._7aske.grain.web.view.View;
import com._7aske.grain.ui.util.Styles;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Map;

public class ErrorPage implements View {
	private final HttpException exception;

	public ErrorPage(HttpException exception) {
		this.exception = exception;
	}

	public static String getDefault(Throwable exception, String path) {
		return new ErrorPage(new HttpException.InternalServerError(exception, path)).getContent();
	}

	public @NotNull String getContent() {
		StringBuilder builder = new StringBuilder();
		builder.append("<html>");
		builder.append("<head>");
		builder.append("<meta charset='utf8'>");
		builder.append(getStyle());
		builder.append("<title>").append(getTitle()).append("</title>");
		builder.append("</head>");
		builder.append("<body>");
		builder.append("<div class=\"error-page\"><div class=\"form\">");
		builder.append("<h1>").append(getTitle()).append("</h1>");
		if (exception.getPath() != null)
			builder.append(getRequestPath());
		builder.append(getDescription());
		if (exception instanceof HttpException.InternalServerError) {
			builder.append("<hr/>").append("<pre>");
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			PrintStream stream = new PrintStream(bos);
			exception.printStackTrace(stream);
			builder.append(bos);
			builder.append("</pre>");
		}
		builder.append(getStatusBar());
		builder.append("</div></div>");
		builder.append("</body>");
		builder.append("</html>");
		return builder.toString();
	}


	@Override
	public @NotNull String getName() {
		return "error.html";
	}

	@Override
	public @NotNull String getContentType() {
		return HttpContentType.TEXT_HTML;
	}

	@NotNull
	@Override
	public Map<String, Object> getAttributes() {
		return Map.of();
	}

	private String getStatusBar() {
		return "<h3>Grain</h3>";
	}

	private String getDescription() {
		return String.format("<h2>Description</h2><p>%s</p>", exception.getMessage() == null ? exception.getStatus().getReason() : exception.getMessage());
	}

	private String getTitle() {
		return String.format("HTTP Status %d - %s", exception.getStatus().getValue(), exception.getStatus().getReason());
	}

	private String getRequestPath() {
		return String.format("<h2>Path %s</h2>", exception.getPath());
	}

	private static String getStyle() {
		return "<style type=\"text/css\">" + Styles.getCommonStyles() + "</style>";
	}
}
