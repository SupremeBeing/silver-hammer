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
package ru.silverhammer.core.processor;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import ru.silverhammer.common.Reflector;
import ru.silverhammer.common.injection.Inject;
import ru.silverhammer.common.injection.Injector;
import ru.silverhammer.core.ProcessorReference;
import ru.silverhammer.core.metadata.UiMetadata;

public class Processor implements IProcessor {

	private final Injector injector;
	
	public Processor(@Inject Injector injector) {
		this.injector = injector;
	}

	@Override
	public void process(UiMetadata metadata, Object data, AnnotatedElement member, Annotation unused) {
		for (Class<?> cl : Reflector.getClassHierarchy(data.getClass())) {
			processAnnotations(metadata, data, cl);
			for (Method method : cl.getDeclaredMethods()) {
				processAnnotations(metadata, data, method);
			}
			for (Field field : cl.getDeclaredFields()) {
				processAnnotations(metadata, data, field);
			}
		}
	}
	
	private void processAnnotations(UiMetadata metadata, Object data, AnnotatedElement element) {
		for (Annotation annotation : element.getAnnotations()) {
			ProcessorReference pr = annotation.annotationType().getAnnotation(ProcessorReference.class);
			if (pr != null) {
				IProcessor processor = injector.instantiate(pr.value());
				processor.process(metadata, data, element, annotation);
			}
		}
	}
}