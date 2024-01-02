package com._7aske.grain.web.http;

public class HttpConstants {
	private HttpConstants(){}
	public static final String CRLF = "\r\n";
	public static final byte[] CRLF_BYTES = CRLF.getBytes();
	public static final int CRLF_LEN = CRLF.length();
	public static final String HTTP_V1 = "HTTP/1.1";
	public static final String HTTP_V2 = "HTTP/2";
	public static final String CHARSET = "charset";
	public static final String HTTP = "http";
}
