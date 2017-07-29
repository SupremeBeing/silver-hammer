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
package ru.silverhammer.core.initializer;

import java.lang.reflect.Field;

import ru.silverhammer.common.Reflector;
import ru.silverhammer.common.injection.Inject;
import ru.silverhammer.core.Caption;
import ru.silverhammer.core.FieldProcessor;
import ru.silverhammer.core.control.IMultiCaptionControl;
import ru.silverhammer.core.initializer.annotation.AnnotatedCaptions;
import ru.silverhammer.core.processor.IStringProcessor;

public class AnnotatedCaptionsInitializer implements IInitializer<IMultiCaptionControl<?>, AnnotatedCaptions> {

	private final IStringProcessor processor;
	private final FieldProcessor fieldProcessor;

	public AnnotatedCaptionsInitializer(@Inject IStringProcessor processor, @Inject FieldProcessor fieldProcessor) {
		this.processor = processor;
		this.fieldProcessor = fieldProcessor;
	}

	@Override
	public void init(IMultiCaptionControl<?> control, AnnotatedCaptions annotation, Object data, Field field) {
		for (Class<?> cl : Reflector.getClassHierarchy(annotation.value())) {
			for (Field f : cl.getDeclaredFields()) {
				if (fieldProcessor.hasControlAnnotation(f)) {
					Caption c = f.getAnnotation(Caption.class);
					control.addCaption(c == null ? "" : processor.getString(c.value()));
				}
			}
		}
	}
}
