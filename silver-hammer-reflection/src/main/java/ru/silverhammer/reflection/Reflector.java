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
package ru.silverhammer.reflection;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

final class Reflector {

	private Reflector() {}
	
	static List<Class<?>> getClassHierarchy(Class<?> cl) {
		List<Class<?>> result = new ArrayList<>();
		while (cl != null) {
			result.add(0, cl);
			cl = cl.getSuperclass();
		}
		return result;
	}

	static <T> T instantiate(Class<T> cl, Object... args) {
		Class<?>[] types = new Class<?>[args.length];
		for (int i = 0; i < args.length; i++) {
			types[i] = args[i].getClass();
		}
		try {
			Constructor<T> ctor = cl.getDeclaredConstructor(types);
			boolean accessible = ctor.isAccessible();
			if (!accessible) {
				ctor.setAccessible(true);
			}
			T result = ctor.newInstance(args);
			if (!accessible) {
				ctor.setAccessible(false);
			}
			return result;
		} catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException | InstantiationException e) {
			throw new RuntimeException(e);
		}
	}
	
	static Object getValue(Object data, Field field) {
		try {
			boolean accessible = field.isAccessible();
			if (!accessible) {
				field.setAccessible(true);
			}
			Object result = field.get(data);
			if (!accessible) {
				field.setAccessible(false);
			}
			return result;
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}
	
	static void setValue(Object data, Field field, Object value) {
		try {
			boolean accessible = field.isAccessible();
			if (!accessible) {
				field.setAccessible(true);
			}
			field.set(data, value);
			if (!accessible) {
				field.setAccessible(false);
			}
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}
	
	static Field findField(Class<?> cl, String fieldName) {
		while (cl != null) {
			for (Field fld : cl.getDeclaredFields()) {
				if (Objects.equals(fieldName, fld.getName())) {
					return fld;
				}
			}
			cl = cl.getSuperclass();
		}
		return null;
	}
	
	static Field[] getFields(Class<?> cl) {
		List<Field> result = new ArrayList<>();
		while (cl != null) {
			for (Field fld : cl.getDeclaredFields()) {
				result.add(fld);
			}
			cl = cl.getSuperclass();
		}
		return result.toArray(new Field[result.size()]);
	}

	static Method[] getMethods(Class<?> cl) {
		List<Method> result = new ArrayList<>();
		while (cl != null) {
			for (Method m : cl.getDeclaredMethods()) {
				result.add(m);
			}
			cl = cl.getSuperclass();
		}
		return result.toArray(new Method[result.size()]);
	}

	static Method[] getInstanceMethods(Class<?> cl) {
		List<Method> result = new ArrayList<>();
		while (cl != null) {
			for (Method m : cl.getDeclaredMethods()) {
				if (!Modifier.isStatic(m.getModifiers())) {
					result.add(m);
				}
			}
			cl = cl.getSuperclass();
		}
		return result.toArray(new Method[result.size()]);
	}

	static Method findMethod(Class<?> cl, String methodName) {
		while (cl != null) {
			for (Method m : cl.getDeclaredMethods()) {
				if (Objects.equals(methodName, m.getName())) {
					return m;
				}
			}
			cl = cl.getSuperclass();
		}
		return null;
	}

	static Object invoke(Object data, Method method, Object... args) {
		try {
			boolean accessible = method.isAccessible();
			if (!accessible) {
				method.setAccessible(true);
			}
			Object result = method.invoke(data, args);
			if (!accessible) {
				method.setAccessible(false);
			}
			return result;
		} catch (InvocationTargetException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}
	
	static Class<?>[] getGenericTypeArguments(Field field) {
		Type t = field.getGenericType();
		if (t instanceof ParameterizedType) {
			Type[] types = ((ParameterizedType) t).getActualTypeArguments();
			Class<?>[] result = new Class[types.length];
			for (int i = 0; i < types.length; i++) {
				if (types[i] instanceof Class) {
					result[i] = (Class<?>) types[i];
				}
			}
		}
		return new Class<?>[0];
	}
}
