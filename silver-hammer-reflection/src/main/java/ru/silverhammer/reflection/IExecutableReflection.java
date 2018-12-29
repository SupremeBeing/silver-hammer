package ru.silverhammer.reflection;

import java.util.List;

public interface IExecutableReflection extends IReflection {

    List<IParameterReflection> getParameters();

}
