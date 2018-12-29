package ru.silverhammer.injection;

public interface IInjector {

    String DEFAULT_NAME = "DEFAULT_BINDING_NAME";

    <T, I extends T> void bind(Class<T> type, String name, I implementation);

    <T, I extends T> void bind(Class<T> type, I implementation);

    <T> void bind(Class<T> type, String name, Class<? extends T> implClass);

    <T> void bind(Class<T> type, Class<? extends T> implClass);

    <T> void unbind(Class<T> type);

    void unbindAll();

}
