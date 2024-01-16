package com._7aske.grain.util;

import java.util.Iterator;
import java.util.Objects;
import java.util.stream.BaseStream;
import java.util.stream.Stream;

public class StreamUtil {
    private StreamUtil(){}

    public static <T> boolean equals(Iterator<T> iter1, Iterator<T> iter2) {
        while (iter1.hasNext() && iter2.hasNext()) {
            if (!Objects.equals(iter1.next(), iter2.next())) {
                return false;
            }
        }

        return !iter1.hasNext() && !iter2.hasNext();
    }

    public static <T> boolean equals(Stream<T> stream1, Stream<T> stream2) {
        return equals(stream1.iterator(), stream2.iterator());
    }

    public static <T> boolean equals(BaseStream<T, ?> stream1, BaseStream<T, ?> stream2) {
        return equals(stream1.iterator(), stream2.iterator());
    }
}
