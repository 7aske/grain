package com._7aske.grain.web.page;

public interface Pageable {
	int getPageNumber();

	int getPageSize();

	default int getPageOffset() {
		return getPageNumber() * getPageSize();
	}
}
