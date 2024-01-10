package com._7aske.grain.ui.impl;

import com._7aske.grain.annotation.NotNull;
import com._7aske.grain.exception.http.HttpException;
import com._7aske.grain.ui.util.Styles;
import com._7aske.grain.web.http.ContentType;
import com._7aske.grain.web.http.HttpStatus;
import com._7aske.grain.web.view.View;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Map;

public class ErrorPage implements View {
	private final Throwable exception;
	private final String path;
	private final HttpStatus status;

	public ErrorPage(Throwable exception, HttpStatus status, String path) {
		this.exception = exception;
		this.status = status;
		this.path = path;
	}

	public static Builder builder() {
		return new Builder();
	}

	public static Builder forException(HttpException ex) {
		return builder()
				.exception(ex)
				.status(ex.getStatus());
	}

	public static String getDefault(Throwable exception, HttpStatus status, String path) {
		return new ErrorPage(exception, status, path).getContent();
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
		if (path != null)
			builder.append(getRequestPath());
		builder.append(getDescription());

		{
			builder.append("<hr/>")
					.append("<pre>");

			ByteArrayOutputStream bos = new ByteArrayOutputStream();

			try (PrintStream stream = new PrintStream(bos)) {
				exception.printStackTrace(stream);
				stream.flush();
			}

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
		return ContentType.TEXT_HTML;
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
		return String.format("<h2>Description</h2><p>%s</p>", exception.getMessage() == null ? status.getReason() : exception.getMessage());
	}

	private String getTitle() {
		return String.format("HTTP Status %d - %s", status.getValue(), status.getReason());
	}

	private String getRequestPath() {
		return String.format("<h2>Path %s</h2>", path);
	}

	private static String getStyle() {
		return "<style type=\"text/css\">" + Styles.getCommonStyles() + "</style>";
	}

	public static final class Builder {
		private Throwable exception;
		private String path;
		private HttpStatus status;

		private Builder() {
		}

		public Builder exception(Throwable exception) {
			this.exception = exception;
			return this;
		}

		public Builder exception(HttpException exception) {
			this.exception = exception;
			this.status = exception.getStatus();
			return this;
		}

		public Builder path(String path) {
			this.path = path;
			return this;
		}

		public Builder status(HttpStatus status) {
			this.status = status;
			return this;
		}

		public ErrorPage build() {
			return new ErrorPage(exception, status, path);
		}
	}
}
