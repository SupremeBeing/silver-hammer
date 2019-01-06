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
import ru.silverhammer.core.processor.IProcessor;
import ru.silverhammer.core.processor.Processor;
import ru.silverhammer.core.resolver.IControlResolver;
import ru.silverhammer.core.string.IStringProcessor;
import ru.silverhammer.core.string.SimpleStringProcessor;
import ru.silverhammer.injection.IInjector;
import ru.silverhammer.injection.Injector;

public final class MetadataCollector {
	
	private final IStringProcessor stringProcessor;
	private final IInjector injector;
	private final FieldProcessor fieldProcessor;
	private final IProcessor<?, ?> processor;

	public MetadataCollector(IControlResolver controlResolver) {
		this(controlResolver, new SimpleStringProcessor(), new Injector());
	}
	
	public MetadataCollector(IControlResolver controlResolver, IStringProcessor stringProcessor, IInjector injector) {
		this.stringProcessor = stringProcessor;
		this.injector = injector;
		fieldProcessor = new FieldProcessor(injector);
		processor = new Processor<>(injector);

		injector.bind(IStringProcessor.class, stringProcessor);
		injector.bind(IControlResolver.class, controlResolver);
		injector.bind(FieldProcessor.class, fieldProcessor);
	}

	// TODO: consider adding error log
	public UiMetadata collect(Object... data) {
		UiMetadata metadata = new UiMetadata(injector, fieldProcessor, stringProcessor);
		for (Object o : data) {
			if (o != null) {
				processor.process(metadata, o, null, null);
			}
		}

		injector.bind(UiMetadata.class, metadata);
		metadata.initialize();
		return metadata;
	}
	
}
