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

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class ClassReflection<T> extends AnnotatedReflection<Class<T>> {
	
	public ClassReflection(Class<T> cl) {
		super(cl);
	}
	
	@SuppressWarnings("unchecked")
	public ConstructorReflection<T>[] getConstructors() {
		Constructor<?>[] cs = getElement().getDeclaredConstructors();
		ConstructorReflection<T>[] result = new ConstructorReflection[cs.length];
		for (int i = 0; i < cs.length; i++) {
			result[i] = new ConstructorReflection<T>((Constructor<T>) cs[i]);
		}
		return result;
	}
	
	public T instantiate(Object... args) {
		Class<?>[] types = new Class<?>[args.length];
		for (int i = 0; i < args.length; i++) {
			types[i] = args[i].getClass();
		}
		ConstructorReflection<T> ctor = findConstructor(types);
		return ctor.invoke(args);
	}
	
	public ConstructorReflection<T> findConstructor(Class<?>... types) {
		try {
			Constructor<T> ctor = getElement().getDeclaredConstructor(types);
			return new ConstructorReflection<>(ctor);
		} catch (NoSuchMethodException e) {
			return null;
		}
	}
	
	public ClassReflection<?>[] getHierarchy() {
		List<ClassReflection<?>> result = new ArrayList<>();
		walkHierarchy(cl -> result.add(0, new ClassReflection<>(cl)));
		return result.toArray(new ClassReflection<?>[result.size()]);
	}
	
	public FieldReflection[] getClassFields() {
		Field[] fields  = getElement().getDeclaredFields();
		FieldReflection[] result = new FieldReflection[fields.length];
		for (int i = 0; i < fields.length; i++) {
			result[i] = new FieldReflection(fields[i]);
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

	// TODO: maintain order from parent to child
	public FieldReflection[] getFields() {
		List<FieldReflection> result = new ArrayList<>();
		walkHierarchy(cl -> {
			for (Field fld : cl.getDeclaredFields()) {
				result.add(new FieldReflection(fld));
			}
		});
		return result.toArray(new FieldReflection[result.size()]);
	}
	
	// TODO: maintain order from parent to child
	public MethodReflection[] getMethods() {
		List<MethodReflection> result = new ArrayList<>();
		walkHierarchy(cl -> {
			for (Method m : cl.getDeclaredMethods()) {
				result.add(new MethodReflection(m));
			}
		});
		return result.toArray(new MethodReflection[result.size()]);
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
	
	private void walkHierarchy(Consumer<Class<?>> consumer) {
		Class<?> cl = getElement();
		while (cl != null) {
			consumer.accept(cl);
			cl = cl.getSuperclass();
		}
	}
}
