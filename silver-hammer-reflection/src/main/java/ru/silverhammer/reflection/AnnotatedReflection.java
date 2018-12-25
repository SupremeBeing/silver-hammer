/*
 * Copyright (c) 2018, Dmitriy Shchekotin
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */
package ru.silverhammer.reflection;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.ArrayList;
import java.util.List;

public abstract class AnnotatedReflection<T extends AnnotatedElement> {
	
	public static class MarkedAnnotation<A extends Annotation> {
		
		private final Annotation annotation;
		private final A marker;
		
		private MarkedAnnotation(Annotation annotation, A marker) {
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

	private final T element;
	
	protected AnnotatedReflection(T element) {
		if (element == null) {
			throw new IllegalArgumentException();
		}
		this.element = element;
	}
	
	protected T getElement() {
		return element;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof MemberReflection) {
			return element.equals(((MemberReflection<?>) obj).getElement());
		}
		return false;
	}

	public Annotation[] getAnnotations() {
		return element.getAnnotations();
	}
	
	public <A extends Annotation> A getAnnotation(Class<A> cl) {
		return element.getAnnotation(cl);
	}
	
	public <A extends Annotation> List<MarkedAnnotation<A>> getMarkedAnnotations(Class<A> markerClass) {
		List<MarkedAnnotation<A>> result = new ArrayList<>();
		for (Annotation annotation : element.getAnnotations()) {
			Class<? extends Annotation> type = annotation.annotationType();
			A marker = type.getAnnotation(markerClass);
			if (marker != null) {	
				result.add(new MarkedAnnotation<>(annotation, marker));
			}
		}
		return result;
	}
}
