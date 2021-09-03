package com._7aske.grain.util;


public class Pair<A, B> {
	private A first;
	private B second;

	public static <A, B> Pair<A, B> of(A first, B second) {
		Pair<A, B> pair = new Pair<>();
		pair.first = first;
		pair.second = second;
		return pair;
	}

	public A getFirst() {
		return first;
	}

	public void setFirst(A first) {
		this.first = first;
	}

	public B getSecond() {
		return second;
	}

	public void setSecond(B second) {
		this.second = second;
	}
}
