package ru.silverhammer.reflection;

public interface IMethodReflection extends IExecutableReflection<Object> {

    boolean isStatic();

    Object invokeOn(Object data, Object... args);

}
