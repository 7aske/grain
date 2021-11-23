package com._7aske.grain.orm.exception;

public class GrainDbNonUniqueResultException extends GrainDbException {
	public GrainDbNonUniqueResultException() {
		super("Query returned multiple values instead of one");
	}
}
