package com._7aske.grain.core.reflect.factory;

import com._7aske.grain.core.component.Grain;
import com._7aske.grain.core.component.Injectable;
import com._7aske.grain.core.component.Order;
import com._7aske.grain.exception.GrainReflectionException;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.SuperMethodCall;

import java.lang.reflect.Method;

@Grain
@Order(Order.HIGHEST_PRECEDENCE + 200)
public class InterfaceGrainFactory implements GrainFactory {
    private static final ClassLoader CLASS_LOADER = Thread.currentThread().getContextClassLoader();

    @Override
    public int getOrder() {
        return Order.HIGHEST_PRECEDENCE + 200;
    }

    @Override
    public boolean supports(Injectable dependency) {
        return dependency.isInterface();
    }

    @Override
    public <T> T create(Injectable dependency, Object[] args) {
        DynamicType.Builder<?> byteBuddy = new ByteBuddy()
                .subclass(Object.class)
                .implement(dependency.getType());

        for (Method method : dependency.getType().getDeclaredMethods()) {
            if (method.isDefault()) {
                byteBuddy = byteBuddy.define(method)
                        .intercept(SuperMethodCall.INSTANCE)
                        .annotateMethod(method.getDeclaredAnnotations());
            }
        }

        try (DynamicType.Unloaded<?> unloaded = byteBuddy.make()) {

            return (T) unloaded.load(CLASS_LOADER)
                    .getLoaded()
                    .getConstructor()
                    .newInstance();
        } catch (Exception e) {
            throw new GrainReflectionException("Failed to create proxy for interface " + dependency.getType(), e);
        }
    }
}
