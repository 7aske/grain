package com._7aske.grain.core.cache;

import com._7aske.grain.core.cache.annotation.CacheEvict;
import com._7aske.grain.gtl.interpreter.Interpreter;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

public class CacheConditionEvaluator {
    private CacheConditionEvaluator() {}

    public static boolean evaluateCondition(Method method, Object... args) {
        String condition = null;
        if (method.isAnnotationPresent(CacheEvict.class)) {
            condition = method.getAnnotation(CacheEvict.class).condition();
        }

        if (condition != null && !condition.isEmpty()) {
            Interpreter interpreter = new Interpreter();
            Parameter[] parameters = method.getParameters();
            for (int i = 0; i < parameters.length; ++i) {
                interpreter.putSymbol(parameters[i].getName(), args[i]);
            }

            return Boolean.parseBoolean(String.valueOf(interpreter.evaluate(condition)));
        }

        return true;
    }
}
