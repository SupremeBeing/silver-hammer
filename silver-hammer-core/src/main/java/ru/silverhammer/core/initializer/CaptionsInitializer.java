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

import ru.silverhammer.core.Caption;
import ru.silverhammer.core.control.IMultiCaptionControl;
import ru.silverhammer.core.control.annotation.Table;
import ru.silverhammer.core.resolver.IControlResolver;
import ru.silverhammer.core.string.IStringProcessor;
import ru.silverhammer.reflection.ClassReflection;
import ru.silverhammer.reflection.IFieldReflection;

public class CaptionsInitializer implements IInitializer<IMultiCaptionControl<?>, Table> {

	private final IStringProcessor processor;
	private final IControlResolver controlResolver;

	public CaptionsInitializer(IStringProcessor processor, IControlResolver controlResolver) {
		this.processor = processor;
		this.controlResolver = controlResolver;
	}

	@Override
	public void init(IMultiCaptionControl<?> control, Table annotation, Object data, IFieldReflection field) {
		if (annotation.annotationCaptions() != Void.class) {
			for (IFieldReflection fr : new ClassReflection<>(annotation.annotationCaptions()).getFields()) {
				if (controlResolver.hasControlAnnotation(fr)) {
					Caption c = fr.getAnnotation(Caption.class);
					control.addCaption(c == null ? fr.getName() : processor.getString(c.value()));
				}
			}
		} else if (annotation.captions().length > 0) {
			for (String caption : annotation.captions()) {
				control.addCaption(processor == null ? caption : processor.getString(caption));
			}
		}
	}
}
