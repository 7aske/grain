package com._7aske.grain.util;

import org.junit.jupiter.api.Test;

import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;

class StreamUtilTest {

    @Test
    void testEquals() {
        Stream<Integer> stream1 = Stream.of(1, 2, 3);
        Stream<Integer> stream2 = Stream.of(1, 2, 3);

        assertTrue(StreamUtil.equals(stream1, stream2));
    }

    @Test
    void testEquals2() {
        IntStream stream1 = IntStream.range(0, 10);
        IntStream stream2 = IntStream.range(0, 10);

        assertTrue(StreamUtil.equals(stream1, stream2));
    }
}