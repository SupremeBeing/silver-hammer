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
import java.util.List;

import ru.silverhammer.core.ProcessorReference;
import ru.silverhammer.core.metadata.UiMetadata;
import ru.silverhammer.injection.IInjector;
import ru.silverhammer.reflection.*;
import ru.silverhammer.reflection.IReflection.MarkedAnnotation;

public class Processor<R extends IReflection, A extends Annotation> implements IProcessor<R, A> {

	private final IInjector injector;
	
	public Processor(IInjector injector) {
		this.injector = injector;
	}

	@Override
	public void process(UiMetadata metadata, Object data, R reflection, A unused) {
		ClassReflection<?> cr = new ClassReflection<>(data.getClass());
		for (ClassReflection<?> cl : cr.getHierarchy()) {
			processAnnotations(metadata, data, cl);
		}
		for (IMethodReflection method : cr.getMethods()) {
			processAnnotations(metadata, data, method);
		}
		for (IFieldReflection field : cr.getFields()) {
			processAnnotations(metadata, data, field);
		}
	}

	@SuppressWarnings("unchecked")
	private void processAnnotations(UiMetadata metadata, Object data, IReflection reflection) {
		List<MarkedAnnotation<ProcessorReference>> marked = reflection.getMarkedAnnotations(ProcessorReference.class);
		for (MarkedAnnotation<ProcessorReference> ma : marked) {
			IProcessor processor = injector.instantiate(ma.getMarker().value());
			processor.process(metadata, data, reflection, ma.getAnnotation());
		}
	}
}
