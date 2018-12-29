package ru.silverhammer.injection;

import ru.silverhammer.reflection.IMethodReflection;

public interface IInjector {

    String DEFAULT_NAME = "DEFAULT_BINDING_NAME";

    <T> void bind(Class<T> type, String name, T implementation);

    <T> void bind(Class<T> type, T implementation);

    <T> void bind(Class<T> type, String name, Class<T> implClass);

    <T> void bind(Class<T> type, Class<T> implClass);

    void unbind(Class<?> type);

    void unbindAll();

    <T> T getInstance(Class<T> type, String name);

    <T> T getInstance(Class<T> type);

    <T> T instantiate(Class<T> type);

    Object invoke(Object data, IMethodReflection method);
}
