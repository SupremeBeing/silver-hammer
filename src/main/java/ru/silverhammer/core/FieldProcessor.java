/*
 * Copyright (c) 2017, Dmitriy Shchekotin
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
package ru.silverhammer.core;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import ru.silverhammer.common.injection.Injector;
import ru.silverhammer.core.control.IControl;
import ru.silverhammer.core.converter.IConverter;
import ru.silverhammer.core.resolver.IControlResolver;
import ru.silverhammer.core.validator.IValidator;

public class FieldProcessor {

	private final Injector injector;
	private final IControlResolver controlResolver;
	
	public FieldProcessor(Injector injector, IControlResolver controlResolver) {
		this.injector = injector;
		this.controlResolver = controlResolver;
	}

	public boolean hasControlAnnotation(Field field) {
		return getControlClass(field) != null;
	}
	
	public Class<? extends IControl<?>> getControlClass(Field field) {
		for (Annotation a : field.getAnnotations()) {
			Class<? extends IControl<?>> controlClass = controlResolver.getControlClass(a.annotationType());
			if (controlClass != null) {
				return controlClass;
			}
		}
		return null;
	}

	public Object getControlValue(Object value, Field field) {
		Annotation[] annotations = field.getAnnotations();
		for (int i = annotations.length - 1; i >= 0; i--) {
			Class<? extends Annotation> type = annotations[i].annotationType();
			ConverterReference cr = type.getAnnotation(ConverterReference.class);
			if (cr != null) {
				@SuppressWarnings("unchecked")
				IConverter<Object, Object, Annotation> converter = (IConverter<Object, Object, Annotation>) injector.instantiate(cr.value());
				value = converter.convertForward(value, annotations[i]);
			}
		}
		return value;
	}
	
	public Object getFieldValue(Object value, Field field) {
		for (Annotation annotation : field.getAnnotations()) {
			Class<? extends Annotation> type = annotation.annotationType();
			ConverterReference cr = type.getAnnotation(ConverterReference.class);
			if (cr != null) {
				@SuppressWarnings("unchecked")
				IConverter<Object, Object, Annotation> converter = (IConverter<Object, Object, Annotation>) injector.instantiate(cr.value());
				value = converter.convertBackward(value, annotation);
			}
		}
		return value;
	}
	
	public Annotation validateValue(Object value, Field field) {
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
