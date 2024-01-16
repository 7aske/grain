package com._7aske.grain.util;

import com._7aske.grain.annotation.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.function.Predicate;

public class ByteBuffer extends OutputStream {
    private java.nio.ByteBuffer buffer;
    private int maxSize;
    private int size;

    public static ByteBuffer allocate(int size) {
        return new ByteBuffer(size);
    }

    private ByteBuffer(int maxSize) {
        this.buffer = java.nio.ByteBuffer.allocate(maxSize);
        this.maxSize = maxSize;
        this.size = 0;
    }

    public int length() {
        return size;
    }

    public byte get(int index) {
        return buffer.get(index);
    }

    public byte[] getBytes() {
        return getBytes(0, size);
    }

    public byte[] getBytes(int start, int len) {
        byte[] res = new byte[len];
        buffer.get(start, res, 0, len);
        return res;
    }

    public boolean equals(int aFrom, int aTo, byte[] bArr, int bFrom, int bTo) {
        if (aTo - aFrom != bTo - bFrom) {
            return false;
        }

        for (int i = aFrom, j = bFrom; i < aTo; i++, j++) {
            if (buffer.get(i) != bArr[j]) {
                return false;
            }
        }

        return true;
    }

    public void reset() {
        setLength(0);
    }

    public void setLength(int size) {
        buffer.position(size);
        this.size = size;
    }

    public int writeUntil(InputStream reader, Predicate<ByteBuffer> condition) throws IOException {
        int written = 0;
        int c;
        do {
            c = reader.read();
            if (c == -1) {
                break;
            }
            write(c);
            written++;
        } while (condition.test(this));

        return written;
    }

    public int writeN(InputStream reader, int n) throws IOException {
        if (n < 0) {
            throw new IllegalArgumentException("n cannot be less than zero");
        }

        int written = 0;
        int c;
        while (n-- > 0) {
            c = reader.read();
            if (c == -1) {
                break;
            }
            write(c);
            written++;
        }
        return written;
    }

    @Override
    public void write(@NotNull byte[] b, int off, int len) {
        buffer.put(b, off, len);
        size += len;
    }

    @Override
    public synchronized void write(int b) {
        ensureCapacity(size + 1);
        buffer.put(size++, (byte) b);
        buffer.position(size);
    }

    public boolean startsWith(byte[] start) {
        if (start.length > size) {
            return false;
        }

        return equals(0, start.length, start, 0, start.length);
    }

    public boolean endsWith(byte[] end) {
        if (end.length > size) {
            return false;
        }

        return equals(size - end.length, size, end, 0, end.length);
    }

    private void ensureCapacity(int newSize) {
        if (newSize >= maxSize) {
            maxSize *= 2;
            buffer = resizeBuffer(buffer, maxSize);
        }
    }

    public void resize(int newSize) {
        if (newSize <= size) {
            return;
        }
        maxSize = newSize;
        buffer = resizeBuffer(buffer, maxSize);
    }

    private static java.nio.ByteBuffer resizeBuffer(final java.nio.ByteBuffer in, int newSize) {
            final java.nio.ByteBuffer result = java.nio.ByteBuffer.allocate(newSize);
            in.flip();
            result.put(in);
            return result;
    }

    @Override
    public String toString() {
        return new String(getBytes(), Charset.defaultCharset());
    }
}
