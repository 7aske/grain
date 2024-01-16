package com._7aske.grain.util.iterator;

public record IteratorPositionInformation(int row, int character) {
    @Override
    public String toString() {
        return String.format("at character %d, row %d", character, row);
    }
}
