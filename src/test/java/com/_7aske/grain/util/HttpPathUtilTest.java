package com._7aske.grain.util;

import org.junit.jupiter.api.Test;

import static com._7aske.grain.util.HttpPathUtil.arePathsMatching;
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
}