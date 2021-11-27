package com._7aske.grain.http.session;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CookieTest {
	@Test
	void testCookieParse() {
		String cookieString = "GSID=298zf09hf012fh2; Max-Age=1000; csrftoken=u32t4o3tb3gg43; _gat=1";
		Map<String, Cookie> cookies = Cookie.parse(cookieString);
		System.err.println(cookies);
		assertEquals(1000L, cookies.get("GSID").getMaxAge());
		assertEquals("298zf09hf012fh2", cookies.get("GSID").getValue());
		assertEquals("u32t4o3tb3gg43", cookies.get("csrftoken").getValue());
		assertEquals("1", cookies.get("_gat").getValue());
	}
}