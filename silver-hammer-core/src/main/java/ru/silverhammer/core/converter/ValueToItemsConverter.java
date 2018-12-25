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
package ru.silverhammer.core.converter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import ru.silverhammer.core.FieldProcessor;
import ru.silverhammer.core.converter.annotation.ValueToItems;
import ru.silverhammer.core.resolver.IControlResolver;
import ru.silverhammer.injection.Inject;
import ru.silverhammer.reflection.ClassReflection;
import ru.silverhammer.reflection.FieldReflection;

public class ValueToItemsConverter implements IConverter<Object, Object, ValueToItems> {

	private final FieldProcessor fieldProcessor;
	private final IControlResolver controlResolver;

	public ValueToItemsConverter(@Inject FieldProcessor fieldProcessor, @Inject IControlResolver controlResolver) {
		this.fieldProcessor = fieldProcessor;
		this.controlResolver = controlResolver;
	}

	@Override
	public Object convertForward(Object source, ValueToItems annotation) {
		if (source != null) {
			List<FieldReflection> fields = collectFields(annotation.value(), annotation);
			if (source instanceof Collection) {
				Collection<Object[]> result = new ArrayList<>();
				for (Object o : (Collection<?>) source) {
					result.add(createItem(o, fields, annotation));
				}
				return result;
			} else {
				return createItem(source, fields, annotation);
			}
		}
		return null;
	}
	
	private Object[] createItem(Object o, List<FieldReflection> fields, ValueToItems annotation) {
		Object[] item = new Object[fields.size()];
		for (int i = 0; i < fields.size(); i++) {
			FieldReflection field = fields.get(i);
			Object value = field.getValue(o);
			item[i] = annotation.annotated() ? fieldProcessor.getControlValue(value, field) : value;
		}
		return item;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Object convertBackward(Object destination, ValueToItems annotation) {
		if (destination != null) {
			List<FieldReflection> fields = collectFields(annotation.value(), annotation);
			if (destination instanceof Collection) {
				@SuppressWarnings("rawtypes")
				ClassReflection<? extends Collection> cr = new ClassReflection<>(annotation.collection());
				Collection<Object> result = cr.findConstructor().invoke();
				for (Object[] row : (Collection<Object[]>) destination) {
					result.add(createObject(row, fields, annotation));
				}
				return result;
			} else if (destination instanceof Object[]) {
				return createObject((Object[]) destination, fields, annotation);
			}
		}
		return null;
	}
	
	private Object createObject(Object[] item, List<FieldReflection> fields, ValueToItems annotation) {
		Object result = new ClassReflection<>(annotation.value()).findConstructor().invoke();
		for (int i = 0; i < fields.size(); i++) {
			FieldReflection field = fields.get(i);
			Object value = item[i];
			if (annotation.annotated()) {
				value = fieldProcessor.getFieldValue(value, field);
			}
			field.setValue(result, value);
		}
		return result;
	}
	
	private List<FieldReflection> collectFields(Class<?> cls, ValueToItems annotation) {
		List<FieldReflection> result = new ArrayList<>();
		ClassReflection<?> reflection = new ClassReflection<>(cls);
		for (FieldReflection fr : reflection.getFields()) {
			if (!annotation.annotated() || controlResolver.hasControlAnnotation(fr)) {
				result.add(fr);
			}
		}
		return result;
	}
}
