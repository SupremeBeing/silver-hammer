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
import ru.silverhammer.core.processor.Processor;
import ru.silverhammer.core.resolver.IControlResolver;
import ru.silverhammer.core.string.IStringProcessor;
import ru.silverhammer.core.string.SimpleStringProcessor;

public class UiGenerator<Container> {
	
	private final Injector injector;
	private final IStringProcessor stringProcessor;
	private final IUiBuilder<Container> builder;
	private final FieldProcessor fieldProcessor;
	private final IControlResolver controlResolver;

	public UiGenerator(IControlResolver controlResolver, IUiBuilder<Container> builder) {
		this(controlResolver, builder, new SimpleStringProcessor());
	}
	
	public UiGenerator(IControlResolver controlResolver, IUiBuilder<Container> builder, IStringProcessor stringProcessor) {
		this.builder = builder;
		this.injector = new Injector();
		
		this.stringProcessor = stringProcessor;
		injector.bind(IStringProcessor.class, stringProcessor);

		this.controlResolver = controlResolver;
		injector.bind(IControlResolver.class, controlResolver);

		this.fieldProcessor = new FieldProcessor(injector);
		injector.bind(FieldProcessor.class, fieldProcessor);
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
		Processor processor = new Processor(injector);
		for (Object o : data) {
			if (o != null) {
				processor.process(metadata, o, null, null);
			}
		}
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
				validateMethods(metadata);
			});
		});
		validateMethods(metadata);
		return builder.buildUi(metadata);
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
	
	public boolean validateMethods(UiMetadata metadata) {
		boolean result = true;
		for (MethodAttributes ma : metadata.getValidators()) {
			Object valid = injector.invoke(ma.getData(), ma.getMethod());
			if (valid instanceof Boolean) {
				result &= (Boolean) valid;
			}
		}
		return result;
	}
	
	public void commit(UiMetadata metadata) {
		metadata.visitControlAttributes((c) -> Reflector.setFieldValue(c.getData(), c.getField(), fieldProcessor.getFieldValue(c.getControl().getValue(), c.getField())));
	}

	public boolean isValid(UiMetadata metadata) {
		return metadata.findControlAttributes((ca) -> ca.getControl() instanceof IValidatableControl && !((IValidatableControl<?>) ca.getControl()).isControlValid()) == null && validateMethods(metadata);		
	}
}
