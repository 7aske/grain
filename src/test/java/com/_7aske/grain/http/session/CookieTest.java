package com._7aske.grain.http.session;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class CookieTest {
	@Test
	void testCookieParseMany() {
		String cookieString = "GSID=298zf09hf012fh2; csrftoken=u32t4o3tb3gg43; _gat=1";
		Cookie cookie = Cookie.parse(cookieString);
		System.err.println(cookie);
		assertNotNull(cookie);
		assertEquals("GSID=298zf09hf012fh2; _gat=1; csrftoken=u32t4o3tb3gg43", cookie.toString());
		assertEquals("GSID", cookie.getName());
		assertEquals("298zf09hf012fh2", cookie.getValue());
		assertEquals("u32t4o3tb3gg43", cookie.get("csrftoken"));
		assertEquals("1", cookie.get("_gat"));
	}
}