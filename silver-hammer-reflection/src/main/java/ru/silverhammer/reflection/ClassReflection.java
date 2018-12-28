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

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ClassReflection<T> extends AnnotatedReflection<Class<T>> {
	
	public ClassReflection(Class<T> cl) {
		super(cl);
	}
	
	@SuppressWarnings("unchecked")
	public List<ConstructorReflection<T>> getConstructors() {
		List<ConstructorReflection<T>> result = new ArrayList<>();
		for (Constructor<?> ctor : getElement().getDeclaredConstructors()) {
			result.add(new ConstructorReflection<>((Constructor<T>) ctor));
		}
		return result;
	}

	public boolean isStatic() {
		return Modifier.isStatic(getElement().getModifiers());
	}

	public boolean isFinal() {
		return Modifier.isFinal(getElement().getModifiers());
	}

	@Override
	public String getName() {
		return getElement().getName();
	}

	@Override
	public Class<?> getType() {
		return getElement();
	}

	public T instantiate(Object... args) {
		Class<?>[] types = new Class<?>[args.length];
		for (int i = 0; i < args.length; i++) {
			Object arg = args[i];
			types[i] = arg == null ? null : arg.getClass();
		}
		ConstructorReflection<T> ctor = findConstructor(types);
		return ctor == null ? null : ctor.invoke(args);
	}
	
	@SuppressWarnings("unchecked")
	public ConstructorReflection<T> findConstructor(Class<?>... types) {
		for (Constructor<?> ctor : getElement().getDeclaredConstructors()) {
			if (match(ctor, types)) {
				return new ConstructorReflection<>((Constructor<T>) ctor);
			}
		}
		return null;
	}
	
	private boolean match(Constructor<?> ctor, Class<?>[] types) {
		Parameter[] params = ctor.getParameters();
		if (types.length != params.length) {
			return false;
		}
		for (int i = 0; i < params.length; i++) {
			Class<?> type = types[i];
			Parameter param = params[i];
			if (type == null) {
				if (param.getType().isPrimitive()) {
					return false;
				}
			} else if (type.isPrimitive()) {
				if (param.getType().isPrimitive() && param.getType() != type) {
					return false;
				} else if (!param.getType().isPrimitive() && !Primitive.match(type, param.getType())) {
					return false;
				}
			} else if (!type.isPrimitive()) {
				if (param.getType().isPrimitive() && !Primitive.match(param.getType(), type)) {
					return false;
				} else if (!param.getType().isPrimitive() && !param.getType().isAssignableFrom(type)) {
					return false;
				}
			}
		}
		return true;
	}
	
	public List<ClassReflection<?>> getHierarchy() {
		List<ClassReflection<?>> result = new ArrayList<>();
		Class<?> cl = getElement();
		while (cl != null) {
			result.add(0, new ClassReflection<>(cl));
			cl = cl.getSuperclass();
		}
		return result;
	}
	
	public List<FieldReflection> getClassFields() {
		List<FieldReflection> result = new ArrayList<>();
		for (Field field : getElement().getDeclaredFields()) {
			result.add(new FieldReflection(field));
		}
		return result;
	}

	public FieldReflection findField(String fieldName) {
		Class<?> cl = getElement();
		while (cl != null) {
			for (Field fld : cl.getDeclaredFields()) {
				if (Objects.equals(fieldName, fld.getName())) {
					return new FieldReflection(fld);
				}
			}
			cl = cl.getSuperclass();
		}
		return null;
	}

	public List<FieldReflection> getFields() {
		List<FieldReflection> result = new ArrayList<>();
		for (ClassReflection<?> cr : getHierarchy()) {
			result.addAll(cr.getClassFields());
		}
		return result;
	}
	
	public List<MethodReflection> getMethods() {
		List<MethodReflection> result = new ArrayList<>();
		for (ClassReflection<?> cr : getHierarchy()) {
			result.addAll(cr.getClassMethods());
		}
		return result;
	}
	
	public List<MethodReflection> getClassMethods() {
		List<MethodReflection> result = new ArrayList<>();
		for (Method method : getElement().getDeclaredMethods()) {
			result.add(new MethodReflection(method));
		}
		return result;
	}

	public MethodReflection findMethod(String methodName) {
		Class<?> cl = getElement();
		while (cl != null) {
			for (Method m : cl.getDeclaredMethods()) {
				if (Objects.equals(methodName, m.getName())) {
					return new MethodReflection(m);
				}
			}
			cl = cl.getSuperclass();
		}
		return null;
	}
}
