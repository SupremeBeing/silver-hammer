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
package ru.silverhammer.model;

import ru.silverhammer.conversion.IStringConverter;
import ru.silverhammer.processor.FieldProcessor;
import ru.silverhammer.injection.IInjector;
import ru.silverhammer.control.IControl;
import ru.silverhammer.reflection.ClassReflection;
import ru.silverhammer.reflection.IFieldReflection;
import ru.silverhammer.reflection.IMethodReflection;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class UiModel {

    private final List<CategoryModel> categories = new ArrayList<>();
    private final List<GroupModel> groups = new ArrayList<>();
    private final List<MethodModel> initializers = new ArrayList<>();
    private final List<MethodModel> validators = new ArrayList<>();

    private final IInjector injector;
    private final FieldProcessor fieldProcessor;
    private final IStringConverter converter;

    public UiModel(IInjector injector, FieldProcessor fieldProcessor, IStringConverter converter) {
        this.injector = injector;
        this.fieldProcessor = fieldProcessor;
        this.converter = converter;
    }

    public List<CategoryModel> getCategories() {
        return categories;
    }

    public List<GroupModel> getGroups() {
        return groups;
    }

    public List<MethodModel> getInitializers() {
        return initializers;
    }

    public List<MethodModel> getValidators() {
        return validators;
    }

    public GroupModel findGroupModel(Predicate<GroupModel> predicate) {
        for (CategoryModel c : categories) {
            for (GroupModel ga : c.getGroups()) {
                if (predicate.test(ga)) {
                    return ga;
                }
            }
        }
        for (GroupModel ga : groups) {
            if (predicate.test(ga)) {
                return ga;
            }
        }
        return null;
    }

    public void visitControlModels(Consumer<ControlModel> consumer) {
        for (CategoryModel c : categories) {
            for (GroupModel ga : c.getGroups()) {
                for (ControlModel ca : ga.getControls()) {
                    consumer.accept(ca);
                }
            }
        }
        for (GroupModel ga : groups) {
            for (ControlModel ca : ga.getControls()) {
                consumer.accept(ca);
            }
        }
    }

    public ControlModel findControlModel(Predicate<ControlModel> predicate) {
        for (CategoryModel c : categories) {
            for (GroupModel ga : c.getGroups()) {
                for (ControlModel ca : ga.getControls()) {
                    if (predicate.test(ca)) {
                        return ca;
                    }
                }
            }
        }
        for (GroupModel ga : groups) {
            for (ControlModel ca : ga.getControls()) {
                if (predicate.test(ca)) {
                    return ca;
                }
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public <T extends IControl<?, ?>> T findControl(Object data, Class<?> type, String fieldName) {
        IFieldReflection field = new ClassReflection<>(type).findField(fieldName);
        if (field != null) {
            ControlModel attrs = findControlModel(ca -> ca.getFieldReflection().equals(field) && ca.getData().equals(data));
            if (attrs != null) {
                return (T) attrs.getControl();
            }
        }
        return null;
    }

    public <T extends IControl<?, ?>> T findControl(Object data, String fieldName) {
        return findControl(data, data.getClass(), fieldName);
    }

    public void commit() {
		visitControlModels(c -> commit(c.getData(), c.getFieldReflection(), c.getControl()));
	}

	private void commit(Object data, IFieldReflection field, IControl<?, ?> control) {
		Object value = fieldProcessor.getFieldValue(control.getValue(), field);
		field.setValue(data, value);
	}

	public boolean isValid() {
		return findControlModel(ca -> !ca.getControl().isControlValid()) == null;
	}

	public void initialize() {
		initializeMethods();
		visitControlModels(ca -> init(ca.getControl(), ca.getFieldReflection()));
		validateMethods();
	}

	private void init(IControl<?, ?> control, IFieldReflection field) {
		validateControl(control, field);
		control.addValueListener(c -> {
			validateControl(control, field);
			validateMethods();
		});
	}

	// TODO: consider exposing full data validation method
	private void validateControl(IControl<?, ?> control, IFieldReflection field) {
		Object value = control.getValue();
		Annotation invalidAnnotation = fieldProcessor.validateValue(value, field);
		String msg = getValidationMessage(invalidAnnotation);
		control.setValidationMessage(msg);
	}

	private void validateMethods() {
		for (MethodModel ma : validators) {
			injector.invoke(ma.getData(), ma.getMethodReflection());
		}
	}

	private void initializeMethods() {
		for (MethodModel ma : initializers) {
			injector.invoke(ma.getData(), ma.getMethodReflection());
		}
	}

    private String getValidationMessage(Annotation annotation) {
        if (annotation != null) {
            List<Object> params = new ArrayList<>();
            String message = null;
            for (IMethodReflection method : new ClassReflection<>(annotation.annotationType()).getMethods()) {
                Object value = method.invokeOn(annotation);
                if ("message".equals(method.getName())) {
                    message = value.toString();
                } else {
                    params.add(value);
                }
            }
            if (message != null) {
                return String.format(converter.getString(message), params.toArray(new Object[0]));
            }
        }
        return null;
    }
}
