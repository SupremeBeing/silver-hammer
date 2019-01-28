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

import ru.silverhammer.model.UiModel;
import ru.silverhammer.resolver.IControlResolver;
import ru.silverhammer.conversion.IStringConverter;
import ru.silverhammer.conversion.SameStringConverter;
import ru.silverhammer.injection.IInjector;
import ru.silverhammer.injection.Injector;

public final class Processor {
	
	private final IStringConverter converter;
	private final IControlResolver controlResolver;

	public Processor(IControlResolver controlResolver) {
		this(controlResolver, new SameStringConverter());
	}
	
	public Processor(IControlResolver controlResolver, IStringConverter converter) {
		this.converter = converter;
		this.controlResolver = controlResolver;
	}

	// TODO: consider adding error log
	public UiModel process(Object... data) {
		IInjector injector = new Injector();
		FieldProcessor fieldProcessor = new FieldProcessor(injector);
		AnnotationProcessor processor = new AnnotationProcessor(injector);
		UiModel model = new UiModel(injector, fieldProcessor, converter);

		injector.bind(IStringConverter.class, converter);
		injector.bind(IControlResolver.class, controlResolver);
		injector.bind(FieldProcessor.class, fieldProcessor);
		injector.bind(UiModel.class, model);

		for (Object o : data) {
			if (o != null) {
				processor.process(o);
			}
		}
		model.initialize();
		return model;
	}
	
}
