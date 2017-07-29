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
package ru.silverhammer.core;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import ru.silverhammer.common.Reflector;
import ru.silverhammer.common.injection.Injector;
import ru.silverhammer.core.control.IControl;
import ru.silverhammer.core.control.IValidatableControl;
import ru.silverhammer.core.initializer.IInitializer;
import ru.silverhammer.core.metadata.MethodAttributes;
import ru.silverhammer.core.metadata.UiMetadata;
import ru.silverhammer.core.processor.IStringProcessor;
import ru.silverhammer.core.processor.SimpleStringProcessor;
import ru.silverhammer.core.resolver.IControlResolver;

public class UiGenerator<Container> {
	
	private final Injector injector;
	private final IStringProcessor stringProcessor;
	private final UiMetadataCollector collector;
	private final IUiBuilder<Container> builder;
	private final FieldProcessor fieldProcessor;
	private final IControlResolver controlResolver;

	public UiGenerator(IControlResolver controlResolver, IUiBuilder<Container> builder) {
		this(controlResolver, builder, new SimpleStringProcessor());
	}

	public UiGenerator(IControlResolver controlResolver, IUiBuilder<Container> builder, IStringProcessor stringProcessor) {
		this.controlResolver = controlResolver;
		this.builder = builder;
		this.injector = new Injector();
		
		this.stringProcessor = stringProcessor;
		injector.bind(IStringProcessor.class, stringProcessor);

		this.fieldProcessor = new FieldProcessor(injector, controlResolver);
		injector.bind(FieldProcessor.class, fieldProcessor);

		this.collector = new UiMetadataCollector(stringProcessor, fieldProcessor, injector);
	}

	public IControlResolver getControlResolver() {
		return controlResolver;
	}

	public Injector getInjector() {
		return injector;
	}

	public IStringProcessor getStringProcessor() {
		return stringProcessor;
	}

	public FieldProcessor getFieldProcessor() {
		return fieldProcessor;
	}

	public Container generate(UiMetadata metadata, Object... data) {
		collector.collect(metadata, data);
		metadata.visitControlAttributes((ca) -> {
			for (Annotation annotation : ca.getField().getAnnotations()) {
				InitializerReference ir = annotation.annotationType().getAnnotation(InitializerReference.class);
				if (ir != null) {
					@SuppressWarnings("unchecked")
					IInitializer<IControl<?>, Annotation> initializer = (IInitializer<IControl<?>, Annotation>) injector.instantiate(ir.value());
					initializer.init(ca.getControl(), annotation, ca.getData(), ca.getField());
				}
			}
			Object value = Reflector.getFieldValue(ca.getData(), ca.getField());
			value = fieldProcessor.getControlValue(value, ca.getField());
			@SuppressWarnings("unchecked")
			IControl<Object> control = (IControl<Object>) ca.getControl();
			control.setValue(value);
		});
		injector.bind(UiMetadata.class, metadata);
		for (MethodAttributes ma : metadata.getInitializers()) {
			injector.invoke(ma.getData(), ma.getMethod());
		}
		metadata.visitControlAttributes((ca) -> {
			validateControl(ca.getControl(), ca.getField());	
			ca.getControl().addControlListener((c) -> {
				validateControl(ca.getControl(), ca.getField());	
				validateMethods(metadata.getValidators());
			});
		});
		validateMethods(metadata.getValidators());
		return builder.buildUi(metadata);
	}
	
	public void commit(UiMetadata metadata) {
		metadata.visitControlAttributes((c) -> {
			Object value = fieldProcessor.getFieldValue(c.getControl().getValue(), c.getField());
			Reflector.setFieldValue(c.getData(), c.getField(), value);
		});
	}

	public boolean isValid(UiMetadata metadata) {
		return null == metadata.findControlAttributes((ca) -> {
			if (ca.getControl() instanceof IValidatableControl) {
				if (!((IValidatableControl<?>) ca.getControl()).isControlValid()) {
					return true;
				}
			}
			return false;
		}) && validateMethods(metadata.getValidators());		
	}

	private boolean validateMethods(Iterable<MethodAttributes> methods) {
		boolean result = true;
		for (MethodAttributes ma : methods) {
			Object valid = injector.invoke(ma.getData(), ma.getMethod());
			if (valid instanceof Boolean) {
				result &= (Boolean) valid;
			}
		}
		return result;
	}

	private void validateControl(IControl<?> control, Field field) {
		Object value = control.getValue();
		Annotation invalidAnnotation = fieldProcessor.validateValue(value, field);
		String msg = getValidationMessage(invalidAnnotation);
		if (control instanceof IValidatableControl) {
			((IValidatableControl<?>) control).setValidationMessage(msg);
		}
	}

	private String getValidationMessage(Annotation annotation) {
		if (annotation != null) {
			List<Object> params = new ArrayList<>();
			String message = null;
			for (Method method : annotation.annotationType().getDeclaredMethods()) {
				Object value = Reflector.invoke(annotation, method);
				if ("message".equals(method.getName())) {
					message = value.toString();
				} else {
					params.add(value);
				}
			}
			if (message != null) {
				return String.format(stringProcessor.getString(message), params.toArray(new Object[params.size()]));
			}
		}
		return null;
	}
}
