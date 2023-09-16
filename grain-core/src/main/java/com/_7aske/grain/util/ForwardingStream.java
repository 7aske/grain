package com._7aske.grain.util;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Optional;
import java.util.Spliterator;
import java.util.function.*;
import java.util.stream.*;

public interface ForwardingStream<T> extends Stream<T> {
    Stream<T> getStream();
    default Stream<T> filter(Predicate<? super T> predicate) { return getStream().filter(predicate); }
    default <R> Stream<R> map(Function<? super T, ? extends R> mapper) { return getStream().map(mapper); }
    default IntStream mapToInt(ToIntFunction<? super T> mapper) { return getStream().mapToInt(mapper); }
    default LongStream mapToLong(ToLongFunction<? super T> mapper) { return getStream().mapToLong(mapper); }
    default DoubleStream mapToDouble(ToDoubleFunction<? super T> mapper) { return getStream().mapToDouble(mapper); }
    default <R> Stream<R> flatMap(Function<? super T, ? extends Stream<? extends R>> mapper) { return getStream().flatMap(mapper); }
    default IntStream flatMapToInt(Function<? super T, ? extends IntStream> mapper) { return getStream().flatMapToInt(mapper); }
    default LongStream flatMapToLong(Function<? super T, ? extends LongStream> mapper) { return getStream().flatMapToLong(mapper); }
    default DoubleStream flatMapToDouble(Function<? super T, ? extends DoubleStream> mapper) { return getStream().flatMapToDouble(mapper); }
    default Stream<T> distinct() { return getStream().distinct(); }
    default Stream<T> sorted() { return getStream().sorted(); }
    default Stream<T> sorted(Comparator<? super T> comparator) { return getStream().sorted(comparator); }
    default Stream<T> peek(Consumer<? super T> action) { return getStream().peek(action); }
    default Stream<T> limit(long maxSize) { return getStream().limit(maxSize); }
    default Stream<T> skip(long n) { return getStream().skip(n); }
    default void forEach(Consumer<? super T> action) { getStream().forEach(action); }
    default void forEachOrdered(Consumer<? super T> action) { getStream().forEachOrdered(action); }
    default Object[] toArray() { return getStream().toArray(); }
    default <A> A[] toArray(IntFunction<A[]> generator) { return getStream().toArray(generator); }
    default T reduce(T identity, BinaryOperator<T> accumulator) { return getStream().reduce(identity, accumulator); }
    default Optional<T> reduce(BinaryOperator<T> accumulator) { return getStream().reduce(accumulator); }
    default <U> U reduce(U identity, BiFunction<U, ? super T, U> accumulator, BinaryOperator<U> combiner) { return getStream().reduce(identity, accumulator, combiner); }
    default <R> R collect(Supplier<R> supplier, BiConsumer<R, ? super T> accumulator, BiConsumer<R, R> combiner) { return getStream().collect(supplier, accumulator, combiner); }
    default <R, A> R collect(Collector<? super T, A, R> collector) { return getStream().collect(collector); }
    default Optional<T> min(Comparator<? super T> comparator) { return getStream().min(comparator); }
    default Optional<T> max(Comparator<? super T> comparator) { return getStream().max(comparator); }
    default long count() { return getStream().count(); }
    default boolean anyMatch(Predicate<? super T> predicate) { return getStream().anyMatch(predicate); }
    default boolean allMatch(Predicate<? super T> predicate) { return getStream().allMatch(predicate); }
    default boolean noneMatch(Predicate<? super T> predicate) { return getStream().noneMatch(predicate); }
    default Optional<T> findFirst() { return getStream().findFirst(); }
    default Optional<T> findAny() { return getStream().findAny(); }
    default Spliterator<T> spliterator() { return getStream().spliterator(); }
    default Iterator<T> iterator() { return getStream().iterator(); }
    default boolean isParallel() { return getStream().isParallel(); }
    default Stream<T> sequential() { return getStream().sequential(); }
    default Stream<T> parallel() { return getStream().parallel(); }
    default Stream<T> unordered() { return getStream().unordered(); }
    default Stream<T> onClose(Runnable closeHandler) { return getStream().onClose(closeHandler); }
    default void close() { getStream().close(); }
}