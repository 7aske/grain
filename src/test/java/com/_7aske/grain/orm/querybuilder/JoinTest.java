package com._7aske.grain.orm.querybuilder;

import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertTrue;

class JoinTest {
	@Test
	void testJoin() {
		Join<?, ?> join = Join.from("source", "target_fk", "target", "target_id", Collections.emptyList());
		Join<?, ?> join2 = Join.from("source", "target_fk", "target", "target_id", Collections.emptyList());
		String sql = join.getSql();
		Pattern pattern = Pattern.compile("left join target target_\\d+ on source.target_fk = target_\\d+.target_id");
		String sql2 = join2.getSql();
		System.err.println(sql);
		System.err.println(sql2);
		assertTrue(pattern.matcher(sql).find());
		assertTrue(pattern.matcher(sql2).find());
	}

}