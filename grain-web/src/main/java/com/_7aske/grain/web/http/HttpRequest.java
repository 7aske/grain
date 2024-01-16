package com._7aske.grain.web.http;

import com._7aske.grain.web.http.multipart.Part;
import com._7aske.grain.web.http.session.Cookie;
import com._7aske.grain.web.http.session.Session;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.*;

// @Incomplete
/**
 * Interface representing HTTP request which aims to be as close to the Servlet API as possible.
 */
public interface HttpRequest {
	Object getAttribute(String name);

	void setAttribute(String name, Object o);

	void removeAttribute(String name);

	Set<String> getAttributeNames();

	String getCharacterEncoding();

	void setCharacterEncoding(String encoding) throws UnsupportedEncodingException;

	long getContentLength();

	String getContentType();

	InputStream getInputStream() throws IOException;

	String getParameter(String name);

	Set<String> getParameterNames();

	String[] getParameterValues(String name);

	Map<String, String[]> getParameterMap();

	String getProtocol();

	String getScheme();

	String getServerName();

	int getServerPort();

	BufferedReader getReader() throws IOException;

	String getRemoteAddr();

	String getRemoteHost();

	Locale getLocale();

	List<Locale> getLocales();

	boolean isSecure();

//	RequestDispatcher getRequestDispatcher(String path);

	int getRemotePort();

	String getLocalName();

	String getLocalAddr();

	int getLocalPort();

//	ServletContext getServletContext();

//	AsyncContext startAsync() throws IllegalStateException;

//	AsyncContext startAsync(ServletRequest servletRequest, ServletResponse servletResponse)
//			throws IllegalStateException;

//	boolean isAsyncStarted();
//
//	boolean isAsyncSupported();

//	AsyncContext getAsyncContext();

//	DispatcherType getDispatcherType();

	String getRequestId();

//	String getProtocolRequestId();

//	ServletConnection getServletConnection();

//	String getAuthType();

	Cookie[] getCookies();

	long getDateHeader(String name);

	String getHeader(String name);

	default String[] getHeaders(String name) {
		return new String[]{getHeader(name)};
	}

	Set<String> getHeaderNames();

	int getIntHeader(String name);

//	default HttpServletMapping getHttpServletMapping() {
//		return new HttpServletMapping() {
//
//			@Override
//			public String getMatchValue() {
//				return "";
//			}
//
//			@Override
//			public String getPattern() {
//				return "";
//			}
//
//			@Override
//			public String getServletName() {
//				return "";
//			}
//
//			@Override
//			public MappingMatch getMappingMatch() {
//				return null;
//			}
//		};
//	}

	HttpMethod getMethod();

//	String getPathInfo();

//	String getPathTranslated();

//	String getContextPath();

	String getQueryString();

	String getRemoteUser();

//	boolean isUserInRole(String role);

//	java.security.Principal getUserPrincipal();

//	String getRequestedSessionId();

	String getRequestURI();

	StringBuffer getRequestURL();

	String getPath();

	Session getSession(boolean create);

	Session getSession();

//	String changeSessionId();

//	boolean isRequestedSessionIdValid();

//	boolean isRequestedSessionIdFromCookie();

//	boolean isRequestedSessionIdFromURL();

//	boolean authenticate(HttpServletResponse response) throws IOException, ServletException;

//	void login(String username, String password) throws ServletException;

//	void logout() throws ServletException;

	Collection<Part> getParts();

	Part getPart(String name);

//	<T extends HttpUpgradeHandler> T upgrade(Class<T> httpUpgradeHandlerClass)
//			throws IOException, ServletException;

//	default Map<String,String> getTrailerFields() {
//		return Collections.emptyMap();
//	}

	default boolean isTrailerFieldsReady() {
		return true;
	}
}
