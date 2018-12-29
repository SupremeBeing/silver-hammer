package ru.silverhammer.reflection;

import java.lang.annotation.Annotation;
import java.util.List;

public interface IReflection {

    class MarkedAnnotation<A extends Annotation> {

        private final Annotation annotation;
        private final A marker;

        protected MarkedAnnotation(Annotation annotation, A marker) {
            this.annotation = annotation;
            this.marker = marker;
        }

        public Annotation getAnnotation() {
            return annotation;
        }

        public A getMarker() {
            return marker;
        }
    }

    String getName();

    Class<?> getType();

    Annotation[] getAnnotations();

    <A extends Annotation> A getAnnotation(Class<A> type);

    <A extends Annotation> List<MarkedAnnotation<A>> getMarkedAnnotations(Class<A> markerClass);

}
