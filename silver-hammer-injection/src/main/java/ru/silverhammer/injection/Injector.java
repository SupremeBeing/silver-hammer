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
package ru.silverhammer.injection;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import ru.silverhammer.reflection.ClassReflection;
import ru.silverhammer.reflection.ConstructorReflection;
import ru.silverhammer.reflection.ExecutableReflection;
import ru.silverhammer.reflection.MethodReflection;
import ru.silverhammer.reflection.ParameterReflection;

public class Injector implements IInjector {

	private final Map<Class<?>, Map<String, Supplier<?>>> bindings = new HashMap<>();

	public Injector() {
		bind(Injector.class, this);
	}

	@Override
	public <T, I extends T> void bind(Class<T> type, String name, I impl) {
		if (type != null && impl != null) {
			Map<String, Supplier<?>> namedBindings = bindings.computeIfAbsent(type, k -> new HashMap<>());
			namedBindings.put(name, () -> impl);
		}
	}

	@Override
	public <T> void bind(Class<T> type, String name, Class<? extends T> implClass) {
		if (name != null && implClass != null) {
			Map<String, Supplier<?>> namedBindings = bindings.computeIfAbsent(type, k -> new HashMap<>());
			namedBindings.put(name, () -> instantiate(implClass));
		}
	}

	@Override
	public <T, I extends T> void bind(Class<T> type, I impl) {
		bind(type, DEFAULT_NAME, impl);
	}

	@Override
	public <T> void bind(Class<T> type, Class<? extends T> implClass) {
		bind(type, DEFAULT_NAME, implClass);
	}

	@Override
	public <T> void unbind(Class<T> type) {
		bindings.remove(type);
	}

	@Override
	public void unbindAll() {
		bindings.clear();
	}

	public <T> T instantiate(Class<T> type) {
		List<ConstructorReflection<T>> constructors = new ClassReflection<>(type).getConstructors();
		if (!constructors.isEmpty()) {
			Object[] args = createArguments(constructors.get(0));
			return constructors.get(0).invoke(args);
		}
		return null;
	}

	public Object invoke(Object data, MethodReflection method) {
		Object[] args = createArguments(method);
		return method.invoke(data, args);
	}

	public Object getInstance(Class<?> type, String name) {
		Map<String, Supplier<?>> namedBindings = bindings.get(type);
		if (namedBindings == null) {
			final Map<String, Supplier<?>> subTypedNamedBindings = new HashMap<>();
			bindings.entrySet().stream()
					.filter(e -> type.isAssignableFrom(e.getKey()))
					.map(Map.Entry::getValue)
					.forEach(subTypedNamedBindings::putAll);
			if (subTypedNamedBindings.size() > 0) {
				namedBindings = subTypedNamedBindings;
			}
		}
		if (namedBindings != null) {
			Supplier<?> binding = namedBindings.get(name);
			if (binding != null) {
				return binding.get();
			}
		}
		return null;
	}

	public Object getInstance(Class<?> type) {
		return getInstance(type, DEFAULT_NAME);
	}

	private Object[] createArguments(ExecutableReflection<?> executable) {
		List<ParameterReflection> params = executable.getParameters();
		Object[] result = new Object[params.size()];
		for (int i = 0; i < params.size(); i++) {
			ParameterReflection param = params.get(i);
			Object impl = getInjectedInstance(param, param.getType());
			if (impl == null) {
				impl = instantiate(param.getType());
			}
			result[i] = impl;
		}
		return result;
	}

	private Object getInjectedInstance(ParameterReflection element, Class<?> type) {
		Inject ia = element.getAnnotation(Inject.class);
		if (ia != null) {
			Named na = element.getAnnotation(Named.class);
			String name = na == null ? DEFAULT_NAME : na.value();
			return getInstance(type, name);
		}
		return null;
	}
}
