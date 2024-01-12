package com._7aske.grain.exception;

public class GrainMultipleDependenciesException extends GrainInitializationException {
    public GrainMultipleDependenciesException(String name) {
        super(String.format("Multiple dependencies of name %s found", name));
    }

    public GrainMultipleDependenciesException(Class<?> clazz) {
        super(String.format("Multiple dependencies of type %s found", clazz.getName()));
    }
}
