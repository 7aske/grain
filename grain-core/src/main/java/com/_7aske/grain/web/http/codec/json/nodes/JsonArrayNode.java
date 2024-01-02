package com._7aske.grain.web.http.codec.json.nodes;

import com._7aske.grain.util.ForwardingStream;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import java.util.stream.Stream;

public class JsonArrayNode extends JsonNode implements ForwardingStream<JsonNode>, Collection<JsonNode>, Iterable<JsonNode> {
    public JsonArrayNode() {
        super(new ArrayList<>());
    }

    public JsonNode get(int index) {
        return getValueAsList().get(index);
    }

    @Override
    public int size() {
        return getValueAsList().size();
    }

    @Override
    public boolean isEmpty() {
        return getValueAsList().isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return getValueAsList().contains(o);
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return getValueAsList().toArray(a);
    }


    @Override
    public boolean remove(Object o) {
        return getValueAsList().remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return new HashSet<>(getValueAsList()).containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends JsonNode> c) {
        return false;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return getValueAsList().removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return getValueAsList().retainAll(c);
    }

    @Override
    public void clear() {
        getValueAsList().clear();
    }

    @Override
    public JsonArrayNode asArray() {
        return this;
    }


    @Override
    public Stream<JsonNode> getStream() {
        return getValueAsList().stream();
    }

    @Override
    public void forEach(Consumer<? super JsonNode> action) {
        ForwardingStream.super.forEach(action);
    }

    @Override
    public Object[] toArray() {
        return ForwardingStream.super.toArray();
    }

    @Override
    public <A> A[] toArray(IntFunction<A[]> generator) {
        return ForwardingStream.super.toArray(generator);
    }

    @Override
    public boolean add(JsonNode jsonNode) {
        return getValueAsList().add(jsonNode);
    }

    @Override
    public Spliterator<JsonNode> spliterator() {
        return ForwardingStream.super.spliterator();
    }

    @Override
    public Iterator<JsonNode> iterator() {
        return ForwardingStream.super.iterator();
    }

}
