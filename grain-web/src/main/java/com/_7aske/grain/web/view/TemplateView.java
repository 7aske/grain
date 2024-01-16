package com._7aske.grain.web.view;

import com._7aske.grain.annotation.NotNull;
import com._7aske.grain.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;

public class TemplateView extends FileView {
	private Map<String, Object> data = null;

	public TemplateView(String path) {
		super(path);
	}

	public static Builder builder(String view) {
		return new Builder(view);
	}

	@Override
	public void addAttribute(@NotNull String key, @Nullable Object value) {
		if (this.data == null) {
			this.data = new HashMap<>();
		}

		this.data.put(key, value);
	}

	@Override
	public void addAttributes(@Nullable Map<String, Object> data) {
		if (this.data == null) {
			this.data = new HashMap<>();
		}

		if (data != null)
			this.data.putAll(data);
	}

	@Override
	public @NotNull Map<String, Object> getAttributes() {
		return data;
	}

	public static final class Builder {
		private final TemplateView templateView;

		private Builder(String view) {
			templateView = new TemplateView(view);
		}

		public static Builder builder(String view) {
			return new Builder(view);
		}

		public Builder withAttribute(String name, Object value) {
			templateView.addAttribute(name, value);
			return this;
		}

		public TemplateView build() {
			return templateView;
		}
	}
}
