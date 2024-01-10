package com._7aske.grain.util;

import com._7aske.grain.GrainApp;
import com._7aske.grain.core.component.Order;
import com._7aske.grain.core.component.Ordered;

import java.lang.reflect.Method;
import java.util.Comparator;
import java.util.function.Function;

public class By {
    private By() {
    }

    public static int order(Order o1, Order o2) {
        int o1Value = o1 != null ? o1.value() : Order.DEFAULT;
        int o2Value = o2 != null ? o2.value() : Order.DEFAULT;
        return Integer.compare(o1Value, o2Value);
    }

    public static int order(Class<?> c1, Class<?> c2) {
        return order(c1.getAnnotation(Order.class), c2.getAnnotation(Order.class));
    }

    public static int order(Method m1, Method m2) {
        return order(m1.getAnnotation(Order.class), m2.getAnnotation(Order.class));
    }

    public static int objectOrder(Object o1, Object o2) {
        return order(o1.getClass().getAnnotation(Order.class), o2.getClass().getAnnotation(Order.class));
    }

    public static <T extends Ordered> Comparator<T> order() {
        return Comparator.comparingInt(T::getOrder);
    }

    public static <T> Comparator<T> objectPackages(Function<? super T, Object> keyExtractor) {
        return Comparator.comparing(keyExtractor, By::packages);
    }

    public static <T> Comparator<T> packages(Function<? super T, ? extends Class<?>> keyExtractor) {
        return Comparator.comparing(keyExtractor, By::packages);
    }

    public static int packages(Class<?> clazz1, Class<?> clazz2) {
        return packages(clazz1.getPackageName(), clazz2.getPackageName());
    }

    public static int packages(Object o1, Object o2) {
        return packages(o1.getClass().getPackageName(), o2.getClass().getPackageName());
    }

    /**
     * Comparator that is used for sorting classes or objects by its packageName
     * so that the resulting sorted list starts with classes or objects that are
     * not grain-library defined.
     *
     * @param c1Package comparable first argument package
     * @param c2Package comparable second argument package
     * @return compared packages
     */
    public static int packages(String c1Package, String c2Package) {
        String basePackagePrefix = GrainApp.class.getPackageName() + ".";
        // @Refactor can this be done better?
        if (!c1Package.startsWith(basePackagePrefix) && !c2Package.startsWith(basePackagePrefix))
            return 0;
        if (c1Package.startsWith(basePackagePrefix) && c2Package.startsWith(basePackagePrefix))
            return 0;
        if (c1Package.startsWith(basePackagePrefix)) return 1;
        if (c2Package.startsWith(basePackagePrefix)) return -1;
        return 0;
    }
}
