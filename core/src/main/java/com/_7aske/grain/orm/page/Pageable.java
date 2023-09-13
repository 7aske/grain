package com._7aske.grain.orm.page;

public interface Pageable {
	int getPageNumber();

	int getPageSize();

	default int getPageOffset() {
		return getPageNumber() * getPageSize();
	}
}
