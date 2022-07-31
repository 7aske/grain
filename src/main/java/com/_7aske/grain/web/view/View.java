package com._7aske.grain.web.view;

/**
 * Basic interface describing the semantics of a View response
 */
public interface View {
	/**
	 * @return String that is going to be written to the HttpResponse.
	 */
	String getContent();

	/**
	 * @return content type of the response. Should be by default text/plain
	 * but the user is free to set any other value.
	 */
	String getContentType();
}
