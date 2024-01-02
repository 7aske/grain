package com._7aske.grain.web.http;

import com._7aske.grain.web.http.session.Cookie;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Locale;

// @Incomplete
/**
 * Interface representing HTTP response which aims to be as close to the Servlet API as possible.
 */
public interface HttpResponse {
    String getCharacterEncoding();

    String getContentType();

    OutputStream getOutputStream() throws IOException;

    PrintWriter getWriter() throws IOException;

    void setCharacterEncoding(String charset);

    void setContentLength(int len);

    void setContentLengthLong(long length);

    void setContentType(String type);

    void setBufferSize(int size);

    int getBufferSize();

    void flushBuffer() throws IOException;

    void resetBuffer();

    boolean isCommitted();

    void reset();

    void setLocale(Locale loc);

    Locale getLocale();

    void addCookie(Cookie cookie);

    boolean containsHeader(String name);

    //	String encodeURL(String url);
//	String encodeRedirectURL(String url);
    void sendError(int sc, String msg) throws IOException;

    void sendError(int sc) throws IOException;

    void sendRedirect(String location) throws IOException;

    void setDateHeader(String name, long date);

    void addDateHeader(String name, long date);

    void setHeader(String name, String value);

    void addHeader(String name, String value);

    void setIntHeader(String name, int value);

    void addIntHeader(String name, int value);

    void setStatus(int sc);

    void setStatus(HttpStatus sc);

    int getStatus();

    String getHeader(String name);

    Collection<String> getHeaders(String name);

    Collection<String> getHeaderNames();
//	default void setTrailerFields(Supplier<Map<String,String>> supplier) {
//	}
//
//	default Supplier<Map<String,String>> getTrailerFields() {
//		return null;
//	}
}
