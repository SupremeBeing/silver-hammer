package ru.silverhammer.reflection;

public interface IConstructorReflection<T> extends IExecutableReflection {

    T invoke(Object... args);

}