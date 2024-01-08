package com._7aske.grain.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ByteBufferTest {

    ByteBuffer buffer;

    @BeforeEach
    void setup() {
        buffer = ByteBuffer.allocate(10);
    }

    @Test
    void test_write() {
        for (int i = 0; i < 10; ++i) {
            buffer.write(i);
        }

        byte[] data = buffer.getBytes();
        for (int i = 0; i < 10; ++i) {
            assertEquals(data[i], i);
        }
    }

    @Test
    void test_writeArray() {
        for (int i = 0; i < 10; ++i) {
            buffer.write(i);
        }

        buffer.write(new byte[]{1, 2, 3}, 1, 2);

        byte[] data = buffer.getBytes();

        assertArrayEquals(
                new byte[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 2, 3},
                data
        );
    }

}