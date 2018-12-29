package ru.silverhammer.reflection;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

class ArrayConstructorReflection<T> implements IConstructorReflection<T> {

    private final Class<?> arrayType;

    protected ArrayConstructorReflection(Class<?> arrayType) {
        if (arrayType == null || !arrayType.isArray()) {
            throw new IllegalArgumentException();
        }
        this.arrayType = arrayType;
    }

    @Override
    public List<IParameterReflection> getParameters() {
        List<IParameterReflection> result = new ArrayList<>();
        result.add(new VirtualParameterReflection(int.class, "size"));
        return result;
    }

    @SuppressWarnings("unchecked")
    @Override
    public T invoke(Object... args) {
        if (args.length != 1 || !(args[0] instanceof Integer)) {
            throw new RuntimeException();
        }
        return (T) Array.newInstance(arrayType.getComponentType(), (Integer) args[0]);
    }

    @Override
    public String getName() {
        return arrayType.getName();
    }

    @Override
    public Class<?> getType() {
        return arrayType;
    }

    @Override
    public Annotation[] getAnnotations() {
        return new Annotation[0];
    }

    @Override
    public <A extends Annotation> A getAnnotation(Class<A> type) {
        return null;
    }

    @Override
    public <A extends Annotation> List<MarkedAnnotation<A>> getMarkedAnnotations(Class<A> markerClass) {
        return null;
    }

    @Override
    public int hashCode() {
        return arrayType.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ArrayConstructorReflection) {
            return Objects.equals(arrayType, ((ArrayConstructorReflection) obj).getType());
        }
        return false;
    }
}