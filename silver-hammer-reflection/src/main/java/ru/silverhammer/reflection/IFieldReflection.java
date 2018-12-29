package ru.silverhammer.reflection;

import java.util.List;

public interface IFieldReflection extends IReflection {

    boolean isStatic();

    Object getStaticValue();

    Object getValue(Object data);

    void setStaticValue(Object value);

    void setValue(Object data, Object value);

    List<ClassReflection<?>> getGenericClasses();

}
