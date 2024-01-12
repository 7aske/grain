package com._7aske.grain.exception;

public class GrainDependencyNotFoundException extends GrainInitializationException {
    public GrainDependencyNotFoundException(String name) {
        super(String.format("Grain dependency of name %s not found", name));
    }

    public GrainDependencyNotFoundException(Class<?> clazz) {
        super(String.format("Grain dependency of type %s not found", clazz.getName()));
    }
}
