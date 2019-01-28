/*
 * Copyright (c) 2019, Dmitriy Shchekotin
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
package ru.silverhammer.processor;

import java.lang.annotation.Annotation;
import java.util.List;

import ru.silverhammer.converter.ConverterReference;
import ru.silverhammer.converter.IConverter;
import ru.silverhammer.validator.IValidator;
import ru.silverhammer.validator.ValidatorReference;
import ru.silverhammer.injection.IInjector;
import ru.silverhammer.reflection.IFieldReflection;
import ru.silverhammer.reflection.IReflection.MarkedAnnotation;

public class FieldProcessor {

	private final IInjector injector;
	
	public FieldProcessor(IInjector injector) {
		this.injector = injector;
	}

	public Object getControlValue(Object value, IFieldReflection field) {
		List<MarkedAnnotation<ConverterReference>> marked = field.getMarkedAnnotations(ConverterReference.class);
		for (int i = marked.size() - 1; i >= 0; i--) {
			MarkedAnnotation<ConverterReference> ma = marked.get(i);
			@SuppressWarnings("unchecked")
			IConverter<Object, Object, Annotation> converter = (IConverter<Object, Object, Annotation>) injector.instantiate(ma.getMarker().value());
			value = converter.convertForward(value, ma.getAnnotation());
		}
		return value;
	}
	
	public Object getFieldValue(Object value, IFieldReflection field) {
		List<MarkedAnnotation<ConverterReference>> marked = field.getMarkedAnnotations(ConverterReference.class);
		for (MarkedAnnotation<ConverterReference> ma : marked) {
			@SuppressWarnings("unchecked")
			IConverter<Object, Object, Annotation> converter = (IConverter<Object, Object, Annotation>) injector.instantiate(ma.getMarker().value());
			value = converter.convertBackward(value, ma.getAnnotation());
		}
		return value;
	}
	
	public Annotation validateValue(Object value, IFieldReflection field) {
		for (Annotation annotation : field.getAnnotations()) {
			for (Annotation metaAnnotation : annotation.annotationType().getAnnotations()) {
				if (metaAnnotation instanceof ConverterReference) {
					ConverterReference cr = (ConverterReference) metaAnnotation;
					@SuppressWarnings("unchecked")
					IConverter<Object, Object, Annotation> converter = (IConverter<Object, Object, Annotation>) injector.instantiate(cr.value());
					value = converter.convertBackward(value, annotation);
				} else if (metaAnnotation instanceof ValidatorReference) {
					ValidatorReference vr = (ValidatorReference) metaAnnotation;
					@SuppressWarnings("unchecked")
					IValidator<Annotation> validator = (IValidator<Annotation>) injector.instantiate(vr.value());
					if (!validator.validate(value, annotation)) {
						return annotation;
					}
				}
			}
		}
		return null;
	}
}
