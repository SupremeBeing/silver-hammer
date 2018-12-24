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
import java.lang.reflect.Modifier;
import java.util.List;

public class ClassReflection<T> extends AnnotatedReflection<Class<T>> {
	
	public ClassReflection(Class<T> cl) {
		super(cl);
	}
	
	@SuppressWarnings("unchecked")
	public ConstructorReflection<T> getDefaultConstructor() {
		Constructor<?>[] cs = getElement().getConstructors();
		// TODO: search for constructor with smallest argument list
		if (cs.length > 0) {
			return new ConstructorReflection<>((Constructor<T>) cs[0]);
		}
		return null;
	}
	
	public T instantiate(Object... args) {
		return Reflector.instantiate(getElement(), args);
	}
	
	public ClassReflection<?>[] getHierarchy() {
		List<Class<?>> classes = Reflector.getClassHierarchy(getElement());
		ClassReflection<?>[] result = new ClassReflection[classes.size()];
		for (int i = 0; i < classes.size(); i++) {
			result[i] = new ClassReflection<>(classes.get(i));
		}
		return result;
	}
	
	public FieldReflection[] getClassFields() {
		return convertFields(getElement().getDeclaredFields());
	}

	public FieldReflection findField(String fieldName) {
		Field field = Reflector.findField(getElement(), fieldName);
		return field == null ? null : createReflection(field);
	}

	// TODO: maintain order from parent to child
	public FieldReflection[] getFields() {
		return convertFields(Reflector.getFields(getElement()));
	}
	
	private FieldReflection[] convertFields(Field[] fields) {
		FieldReflection[] result = new FieldReflection[fields.length];
		for (int i = 0; i < fields.length; i++) {
			result[i] = createReflection(fields[i]);
		}
		return result;
	}
	
	private FieldReflection createReflection(Field field) {
		if (Modifier.isStatic(field.getModifiers())) {
			return new StaticFieldReflection(field);
		} else {
			return new InstanceFieldReflection(field);
		}
	}

	private MethodReflection createReflection(Method method) {
		if (Modifier.isStatic(method.getModifiers())) {
			return new StaticMethodReflection(method);
		} else {
			return new InstanceMethodReflection(method);
		}
	}

	// TODO: maintain order from parent to child
	public MethodReflection[] getMethods() {
		Method[] methods = Reflector.getMethods(getElement());
		MethodReflection[] result = new MethodReflection[methods.length];
		for (int i = 0; i < methods.length; i++) {
			result[i] = createReflection(methods[i]);
		}
		return result;
	}

	public InstanceMethodReflection[] getInstanceMethods() {
		Method[] methods = Reflector.getInstanceMethods(getElement());
		InstanceMethodReflection[] result = new InstanceMethodReflection[methods.length];
		for (int i = 0; i < methods.length; i++) {
			result[i] = new InstanceMethodReflection(methods[i]);
		}
		return result;
	}

	public MethodReflection findMethod(String methodName) {
		Method method = Reflector.findMethod(getElement(), methodName);
		return method == null ? null : createReflection(method);
	}
}
