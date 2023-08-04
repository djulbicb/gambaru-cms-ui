package com.example.gambarucmsui.ports;

import java.util.HashSet;
import java.util.Set;

public class Container {
    private final static Set<Object> beans = new HashSet<>();

    public static void addBean(Object object) {
        beans.add(object);
    }

    public static <T> T getBean(Class<T> beanClass) {
        for (Object bean : beans) {
            if (beanClass.isInstance(bean)) {
                return beanClass.cast(bean);
            }
        }
        throw new RuntimeException("No bean with that class");
    }
}
