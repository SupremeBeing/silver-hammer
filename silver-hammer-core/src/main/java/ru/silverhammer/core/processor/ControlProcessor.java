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
import java.util.Objects;

import ru.silverhammer.core.Caption;
import ru.silverhammer.core.Description;
import ru.silverhammer.core.FieldProcessor;
import ru.silverhammer.core.GroupId;
import ru.silverhammer.core.InitializerReference;
import ru.silverhammer.core.control.IControl;
import ru.silverhammer.core.decorator.IDecorator;
import ru.silverhammer.core.initializer.IInitializer;
import ru.silverhammer.core.metadata.ControlAttributes;
import ru.silverhammer.core.metadata.GroupAttributes;
import ru.silverhammer.core.metadata.UiMetadata;
import ru.silverhammer.core.resolver.IControlResolver;
import ru.silverhammer.conversion.IStringConverter;
import ru.silverhammer.injection.IInjector;
import ru.silverhammer.processor.IProcessor;
import ru.silverhammer.reflection.IFieldReflection;
import ru.silverhammer.reflection.IReflection.MarkedAnnotation;

public class ControlProcessor implements IProcessor<IFieldReflection, Annotation> {

	private final IStringConverter converter;
	private final IInjector injector;
	private final IControlResolver controlResolver;
	private final FieldProcessor fieldProcessor;
	private final UiMetadata metadata;
	
	public ControlProcessor(IInjector injector, IStringConverter converter, IControlResolver controlResolver, FieldProcessor fieldProcessor, UiMetadata metadata) {
		this.injector = injector;
		this.converter = converter;
		this.controlResolver = controlResolver;
		this.fieldProcessor = fieldProcessor;
		this.metadata = metadata;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void process(Object data, IFieldReflection reflection, Annotation annotation) {
		Class<? extends IControl<?, ?>> controlClass = controlResolver.getControlClass(annotation.annotationType());
		if (controlClass != null) {
			IControl control = injector.instantiate(controlClass);
			decorateControl(control, data, reflection);
			addControlAttributes(metadata, reflection.getAnnotation(GroupId.class), createControlAttributes(control, data, reflection));
			control.init(annotation);
			initializeControl(control, data, reflection);
		}
	}

	@SuppressWarnings("unchecked")
	private void decorateControl(IControl<?, ?> control, Object data, IFieldReflection field) {
		for (Annotation a : field.getAnnotations()) {
			Class<? extends IDecorator<?, ?>> decoratorClass = controlResolver.getDecoratorClass(a.annotationType());
			if (decoratorClass != null) {
				IDecorator decorator = injector.instantiate(decoratorClass);
				decorator.init(a, data);
				decorator.setControl(control);
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void initializeControl(IControl<?, ?> control, Object data, IFieldReflection field) {
		List<MarkedAnnotation<InitializerReference>> marked = field.getMarkedAnnotations(InitializerReference.class);
		for (MarkedAnnotation<InitializerReference> ma : marked) {
			IInitializer<IControl<?, ?>, Annotation> initializer = (IInitializer<IControl<?, ?>, Annotation>) injector.instantiate(ma.getMarker().value());
			initializer.init(control, ma.getAnnotation(), data, field);
		}
		Object value = field.getValue(data);
		value = fieldProcessor.getControlValue(value, field);
		((IControl<Object, ?>) control).setValue(value);
	}

	private void addControlAttributes(UiMetadata metadata, GroupId gi, ControlAttributes attributes) {
		String groupId = gi == null ? null : gi.value();
		GroupAttributes group = metadata.findGroupAttributes(g -> Objects.equals(g.getId(), groupId));
		if (group == null) {
			group = new GroupAttributes(groupId);
			metadata.addGroupAttributes(group);
		}
		group.addControlAttributes(attributes);
	}
	
	private ControlAttributes createControlAttributes(IControl<?, ?> control, Object data, IFieldReflection field) {
		ControlAttributes result = new ControlAttributes(control, data, field);
		Caption caption = field.getAnnotation(Caption.class);
		Description description = field.getAnnotation(Description.class);
		if (caption != null) {
			result.setCaption(converter.getString(caption.value()));
			result.setCaptionLocation(caption.location());
			result.setHorizontalAlignment(caption.horizontalAlignment());
			result.setVerticalAlignment(caption.verticalAlignment());
		}
		if (description != null) {
			result.setDescription(converter.getString(description.value()));
		}
		return result;
	}
}
