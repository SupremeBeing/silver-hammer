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

import java.lang.reflect.Array;

import ru.silverhammer.core.metadata.UiMetadata;
import ru.silverhammer.core.processor.annotation.Generatable;
import ru.silverhammer.injection.IInjector;
import ru.silverhammer.reflection.IFieldReflection;

// TODO: consider ignoring structure information or creating separate structure for each instance
public class GeneratableProcessor extends AnnotationProcessor implements IProcessor<IFieldReflection, Generatable> {

	public GeneratableProcessor(IInjector injector) {
		super(injector);
	}

	@Override
	public void process(UiMetadata metadata, Object data, IFieldReflection reflection, Generatable annotation) {
		Object val = reflection.getValue(data);
		if (reflection.getType().isArray()) {
			int length = Array.getLength(val);
			for (int i = 0; i < length; i++) {
				process(metadata, Array.get(val, i));
			}
		} else if (Iterable.class.isAssignableFrom(reflection.getType())) {
			for (Object o : (Iterable<?>) val) {
				process(metadata, o);
			}
		} else {
			process(metadata, val);
		}
	}
}
