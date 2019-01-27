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
package ru.silverhammer.core.metadata;

import ru.silverhammer.core.FieldProcessor;
import ru.silverhammer.processor.AnnotationProcessor;
import ru.silverhammer.core.resolver.IControlResolver;
import ru.silverhammer.conversion.IStringConverter;
import ru.silverhammer.conversion.SameStringConverter;
import ru.silverhammer.injection.IInjector;
import ru.silverhammer.injection.Injector;

public final class MetadataCollector {
	
	private final IStringConverter converter;
	private final IInjector injector;
	private final FieldProcessor fieldProcessor;
	private final AnnotationProcessor processor;

	public MetadataCollector(IControlResolver controlResolver) {
		this(controlResolver, new SameStringConverter(), new Injector());
	}
	
	public MetadataCollector(IControlResolver controlResolver, IStringConverter converter, IInjector injector) {
		this.converter = converter;
		this.injector = injector;
		fieldProcessor = new FieldProcessor(injector);
		processor = new AnnotationProcessor(injector);

		injector.bind(IStringConverter.class, converter);
		injector.bind(IControlResolver.class, controlResolver);
		injector.bind(FieldProcessor.class, fieldProcessor);
	}

	// TODO: consider adding error log
	public UiMetadata collect(Object... data) {
		UiMetadata metadata = new UiMetadata(injector, fieldProcessor, converter);
		injector.bind(UiMetadata.class, metadata);

		for (Object o : data) {
			if (o != null) {
				processor.process(o);
			}
		}

		metadata.initialize();
		return metadata;
	}
	
}
