package com._7aske.grain.util.iterator;

public class IndexedCodepointIterator extends CodepointIterator {
	private int row;
	private int character;

	public IndexedCodepointIterator(String content) {
		super(content);
		this.row = 1;
		this.character = 1;
	}

	@Override
	public Integer next() {
		int val = super.next();
		if (val == '\n') {
			row++;
			character = 1;
		} else {
			character++;
		}
		return val;
	}

	public int getRow() {
		return row;
	}

	public int getCharacter() {
		return character;
	}

	public String getInfo() {
		return String.format("at character %d, row %d", character, row);
	}
}
