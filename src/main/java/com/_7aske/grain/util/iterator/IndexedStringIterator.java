package com._7aske.grain.util.iterator;

public class IndexedStringIterator extends StringIterator {
	private int row;
	private int character;

	public IndexedStringIterator(String content) {
		super(content);
		this.row = 1;
		this.character = 1;
	}

	@Override
	public String next() {
		String val = super.next();
		if (val.equals("\n")) {
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
