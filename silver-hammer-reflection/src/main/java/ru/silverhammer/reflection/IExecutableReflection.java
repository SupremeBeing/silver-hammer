package ru.silverhammer.reflection;

import java.util.List;

public interface IExecutableReflection<T> extends IReflection {

    List<IParameterReflection> getParameters();

    T invoke(Object... args);

}
