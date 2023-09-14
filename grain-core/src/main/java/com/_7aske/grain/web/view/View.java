package com._7aske.grain.web.view;

import com._7aske.grain.annotation.NotNull;
import com._7aske.grain.annotation.Nullable;

import java.util.Map;

/**
 * Basic interface describing the semantics of a View response
 */
public interface View {
	/**
	 * @return String that is going to be written to the HttpResponse.
	 */
	@NotNull String getContent();

	@NotNull String getName();

	/**
	 * @return content type of the response. Should be by default text/plain
	 * but the user is free to set any other value.
	 */
	@NotNull String getContentType();

	default void addAttributes(@Nullable Map<String, Object> data){}

	default void addAttribute(@NotNull String key, @Nullable Object value){};

	default @NotNull Map<String, Object> getAttributes() {return Map.of();};
}
