package com._7aske.grain.web.http;

public class HttpConstants {
	private HttpConstants(){}
	public static final String CRLF = "\r\n";
	public static final String CRLFCRLF = "\r\n\r\n";
	public static final byte[] CRLF_BYTES = CRLF.getBytes();
	public static final byte[] CRLFCRLF_BYTES = CRLFCRLF.getBytes();
	public static final int CRLF_LEN = CRLF.length();
	public static final String BOUNDARY_SEP = "--";
	public static final byte[] BOUNDARY_SEP_BYTES = BOUNDARY_SEP.getBytes();
	public static final int BOUNDARY_SEP_LEN = BOUNDARY_SEP_BYTES.length;
	public static final int DEFAULT_BUFFER_SIZE = 8 * 1024;
	public static final String HTTP_V1 = "HTTP/1.1";
	public static final String HTTP_V2 = "HTTP/2";
	public static final String CHARSET = "charset";
	public static final String NAME = "name";
	public static final String FILENAME = "filename";
	public static final String BOUNDARY = "boundary";
	public static final String HTTP = "http";
}
