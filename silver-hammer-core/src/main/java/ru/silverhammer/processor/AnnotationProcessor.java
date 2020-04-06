/*
 * Copyright (c) 2020, Dmitriy Shchekotin
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

import ru.junkie.IInjector;
import ru.reflexio.*;

public class AnnotationProcessor {

	private final IInjector injector;
	
	public AnnotationProcessor(IInjector injector) {
		this.injector = injector;
	}

	public void process(Object data) {
		TypeReflection<?> reflection = new TypeReflection<>(data.getClass());
		for (ITypeReflection<?> cl : reflection.getTypeHierarchy()) {
			processAnnotations(data, cl);
		}
		for (IInstanceMethodReflection method : reflection.getInstanceMethods()) {
			processAnnotations(data, method);
		}
		for (IInstanceFieldReflection field : reflection.getInstanceFields()) {
			processAnnotations(data, field);
		}
	}

	@SuppressWarnings("unchecked")
	private void processAnnotations(Object data, IReflection reflection) {
		for (MetaAnnotation<ProcessorReference> marked : reflection.getMetaAnnotations(ProcessorReference.class)) {
			IProcessor processor = injector.instantiate(marked.getMetaAnnotation().value());
			processor.process(data, reflection, marked.getAnnotation());
		}
	}
}
