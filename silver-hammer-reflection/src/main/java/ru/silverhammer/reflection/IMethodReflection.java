package ru.silverhammer.reflection;

public interface IMethodReflection extends IExecutableReflection {

    boolean isStatic();

    Object invoke(Object data, Object... args);

    Object invokeStatic(Object... args);

}
