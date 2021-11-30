package com._7aske.grain.orm.page;

public class DefaultPageable implements Pageable {
	public static int DEFAULT_PAGE_SIZE = 10;
	private int page;
	private int count;

	public DefaultPageable(int page) {
		this.page = page;
		this.count = DEFAULT_PAGE_SIZE;
	}

	public DefaultPageable(int page, int count) {
		this.page = page;
		this.count = count;
	}

	@Override
	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	@Override
	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}
}
