package ru.silverhammer.reflection;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

abstract class VirtualReflection implements IReflection {

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
        return new ArrayList<>();
    }

}
