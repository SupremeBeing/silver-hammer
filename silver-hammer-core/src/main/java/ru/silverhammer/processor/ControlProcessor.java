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

import java.lang.annotation.Annotation;
import java.util.List;

import ru.junkie.IInjector;
import ru.reflexio.IFieldReflection;
import ru.reflexio.IInstanceFieldReflection;
import ru.reflexio.MetaAnnotation;
import ru.sanatio.conversion.IStringConverter;
import ru.silverhammer.initializer.InitializerReference;
import ru.silverhammer.model.CategoryModel;
import ru.silverhammer.model.ControlModel;
import ru.silverhammer.model.GroupModel;
import ru.silverhammer.model.UiModel;
import ru.silverhammer.control.IControl;
import ru.silverhammer.decorator.IDecorator;
import ru.silverhammer.initializer.IInitializer;
import ru.silverhammer.resolver.IControlResolver;

public class ControlProcessor implements IProcessor<IInstanceFieldReflection, Annotation> {

	private final IStringConverter converter;
	private final IInjector injector;
	private final IControlResolver controlResolver;
	private final UiModel model;
	
	public ControlProcessor(IInjector injector, IStringConverter converter, IControlResolver controlResolver, UiModel model) {
		this.injector = injector;
		this.converter = converter;
		this.controlResolver = controlResolver;
		this.model = model;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void process(Object data, IInstanceFieldReflection reflection, Annotation annotation) {
		Class<? extends IControl<?, ?>> controlClass = controlResolver.getControlClass(annotation.annotationType());
		if (controlClass != null) {
			IControl control = injector.instantiate(controlClass);
			decorateControl(control, data, reflection);
			addControlAttributes(reflection.getAnnotation(GroupId.class), createControlModel(control, data, reflection));
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
	private void initializeControl(IControl<?, ?> control, Object data, IInstanceFieldReflection field) {
		List<MetaAnnotation<InitializerReference>> marked = field.getMetaAnnotations(InitializerReference.class);
		for (MetaAnnotation<InitializerReference> ma : marked) {
			IInitializer<IControl<?, ?>, Annotation> initializer = (IInitializer<IControl<?, ?>, Annotation>) injector.instantiate(ma.getMetaAnnotation().value());
			initializer.init(control, ma.getAnnotation(), data, field);
		}
		Object value = field.getValue(data);
		value = model.getControlValue(value, field);
		((IControl<Object, ?>) control).setValue(value);
	}

	private void addControlAttributes(GroupId gi, ControlModel controlModel) {
		String groupId = gi == null ? null : gi.value();
		GroupModel groupModel = model.findGroupModel(groupId);
		if (groupModel == null) {
			groupModel = new GroupModel(groupId);
			if (model.getCategories().isEmpty()) {
				model.getCategories().add(new CategoryModel()); // TODO: revisit
			}
			model.getCategories().get(0).getGroups().add(groupModel);
		}
		groupModel.getControls().add(controlModel);
	}
	
	private ControlModel createControlModel(IControl<?, ?> control, Object data, IInstanceFieldReflection field) {
		ControlModel result = new ControlModel(control, data, field);
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
