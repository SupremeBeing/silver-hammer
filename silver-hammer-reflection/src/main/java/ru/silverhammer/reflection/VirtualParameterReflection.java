package ru.silverhammer.reflection;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Objects;

class VirtualParameterReflection implements IParameterReflection {

    private final Class<?> type;
    private final String name;

    protected VirtualParameterReflection(Class<?> type, String name) {
        if (type == null || name == null) {
            throw new IllegalArgumentException();
        }
        this.type = type;
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Class<?> getType() {
        return type;
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
        return Objects.hash(type, name);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof VirtualParameterReflection) {
            VirtualParameterReflection other = (VirtualParameterReflection) obj;
            return Objects.equals(type, other.getType()) && Objects.equals(name, other.getName());
        }
        return false;
    }
}
