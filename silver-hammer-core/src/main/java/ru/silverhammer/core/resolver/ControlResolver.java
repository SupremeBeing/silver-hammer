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
package ru.silverhammer.core.resolver;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

import ru.silverhammer.core.control.IControl;
import ru.silverhammer.core.decorator.IDecorator;
import ru.silverhammer.reflection.IFieldReflection;

public class ControlResolver implements IControlResolver {
	
	private final Map<Class<? extends Annotation>, Class<? extends IControl<?, ?>>> controlMapping = new HashMap<>();
	private final Map<Class<? extends Annotation>, Class<? extends IDecorator<?, ?>>> decoratorMapping = new HashMap<>();

	@Override
	public final Class<? extends IControl<?, ?>> getControlClass(Class<? extends Annotation> annotationClass) {
		return controlMapping.get(annotationClass);
	}

	@Override
	public void bindControl(Class<? extends Annotation> annotationClass, Class<? extends IControl<?, ?>> controlClass) {
		if (annotationClass != null && controlClass != null) {
			controlMapping.put(annotationClass, controlClass);
		}
	}

	@Override
	public Class<? extends IDecorator<?, ?>> getDecoratorClass(Class<? extends Annotation> annotationClass) {
		return decoratorMapping.get(annotationClass);
	}

	@Override
	public void bindDecorator(Class<? extends Annotation> annotationClass, Class<? extends IDecorator<?, ?>> decoratorClass) {
		if (annotationClass != null && decoratorClass != null) {
			decoratorMapping.put(annotationClass, decoratorClass);
		}
	}

	@Override
	public boolean hasControlAnnotation(IFieldReflection fieldReflection) {
		for (Annotation annotation : fieldReflection.getAnnotations()) {
			Class<? extends IControl<?, ?>> controlClass = getControlClass(annotation.annotationType());
			if (controlClass != null) {
				return true;
			}
		}
		return false;
	}
}
