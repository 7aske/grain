package com._7aske.grain.orm.exception;


// Informs the user about dangers of calling update or delete operations
// without specifying a where clause. In case of delete it would truncate
// the table and in case of update it would set the values of all rows
// to the specified value.
public class GrainDbUpdateIdMissingException extends GrainDbException {
	public GrainDbUpdateIdMissingException() {
		super("WARNING: Cannot preform table update operations without specifying a where clause. Danger of corrupting data.");
	}
}
