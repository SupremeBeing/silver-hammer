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

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

class FieldReflection extends MemberReflection<Field> implements IFieldReflection {
	
	protected FieldReflection(Field field) {
		super(field);
	}

	@Override
	public boolean isStatic() {
		return Modifier.isStatic(getElement().getModifiers());
	}

	public boolean isFinal() {
		return Modifier.isFinal(getElement().getModifiers());
	}

	@Override
	public Object getStaticValue() {
		return getValue(null);
	}

	@Override
	public Object getValue(Object data) {
		return forceAccess(() -> {
			try {
				return getElement().get(data);
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			}
		});
	}

	@Override
	public void setStaticValue(Object value) {
		setValue(null, value);
	}

	@Override
	public void setValue(Object data, Object value) {
		forceAccess(() -> {
			try {
				getElement().set(data, value);
				return null;
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			}
		});
	}

	@Override
	public List<ClassReflection<?>> getGenericClasses() {
		Type t = getElement().getGenericType();
		if (t instanceof ParameterizedType) {
			Type[] types = ((ParameterizedType) t).getActualTypeArguments();
			List<ClassReflection<?>> result = new ArrayList<>();
			for (Type type : types) {
				if (type instanceof Class) {
					result.add(new ClassReflection<>((Class<?>) type));
				}
			}
			return result;
		}
		return new ArrayList<>();
	}

	@Override
	public Class<?> getType() {
		return getElement().getType();
	}
}
