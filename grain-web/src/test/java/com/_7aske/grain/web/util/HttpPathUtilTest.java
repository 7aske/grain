package com._7aske.grain.web.util;

import org.junit.jupiter.api.Test;

import static com._7aske.grain.web.util.HttpPathUtil.antMatching;
import static com._7aske.grain.web.util.HttpPathUtil.arePathsMatching;
import static org.junit.jupiter.api.Assertions.*;

class HttpPathUtilTest {

	@Test
	void testArePathsMatching() {
		assertTrue(arePathsMatching("/test", "/test"));
		assertTrue(arePathsMatching("/test/1", "/test/{id}"));
		assertTrue(arePathsMatching("/test/something", "/test"));
		assertTrue(arePathsMatching("/test1/1/test2/2/test3/3", "/test1/{id1}/test2/{id2}/test3/{id3}"));
		assertFalse(arePathsMatching("/nottest", "/test"));
		assertFalse(arePathsMatching("/test/1/", "/test/something"));
	}

	@Test
	void testArePathsAntMatching() {
		assertTrue(antMatching("/**", "/test/foo/bar/baz/something/quux/test/works"));
		assertFalse(antMatching("/test/**/something/*/works", "/test/foo/bar/baz/something/quux/test/works"));
		assertTrue(antMatching("/test/**/something/**/works", "/test/foo/bar/baz/something/quux/test/works"));
		assertFalse(antMatching("/test/*/123", "/test"));
		assertTrue(antMatching("/test/1", "/test/1"));
		assertTrue(antMatching("/test/**/something", "/test/foo/bar/baz/something"));
		assertTrue(antMatching("/test/**/something/**/works", "/test/foo/bar/baz/something/works"));
		assertTrue(antMatching("/test/*/*/test", "/test/1/2/test"));
		assertFalse(antMatching("/not_test", "/test"));
		assertFalse(antMatching("/test/1/", "/test/something"));
	}
}