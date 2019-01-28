/*
 * Copyright (c) 2019, Dmitriy Shchekotin
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
package ru.silverhammer.processor;

import ru.silverhammer.injection.IInjector;
import ru.silverhammer.reflection.ClassReflection;
import ru.silverhammer.reflection.IFieldReflection;
import ru.silverhammer.reflection.IMethodReflection;
import ru.silverhammer.reflection.IReflection;
import ru.silverhammer.reflection.IReflection.MarkedAnnotation;

public class AnnotationProcessor {

	private final IInjector injector;
	
	public AnnotationProcessor(IInjector injector) {
		this.injector = injector;
	}

	public void process(Object data) {
		ClassReflection<?> reflection = new ClassReflection<>(data.getClass());
		for (ClassReflection<?> cl : reflection.getHierarchy()) {
			processAnnotations(data, cl);
		}
		for (IMethodReflection method : reflection.getMethods()) {
			processAnnotations(data, method);
		}
		for (IFieldReflection field : reflection.getFields()) {
			processAnnotations(data, field);
		}
	}

	@SuppressWarnings("unchecked")
	private void processAnnotations(Object data, IReflection reflection) {
		for (MarkedAnnotation<ProcessorReference> marked : reflection.getMarkedAnnotations(ProcessorReference.class)) {
			IProcessor processor = injector.instantiate(marked.getMarker().value());
			processor.process(data, reflection, marked.getAnnotation());
		}
	}
}
