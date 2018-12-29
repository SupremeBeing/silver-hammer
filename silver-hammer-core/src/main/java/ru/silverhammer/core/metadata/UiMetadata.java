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
package ru.silverhammer.core.metadata;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

import ru.silverhammer.core.FieldProcessor;
import ru.silverhammer.core.control.IControl;
import ru.silverhammer.core.control.IValidatableControl;
import ru.silverhammer.core.string.IStringProcessor;
import ru.silverhammer.injection.Injector;
import ru.silverhammer.reflection.*;

public class UiMetadata {

	private final Injector injector;
	private final FieldProcessor fieldProcessor;
	private final IStringProcessor stringProcessor;
	
	private final List<CategoryAttributes> categories = new ArrayList<>();
	private final List<GroupAttributes> groups = new ArrayList<>();
	private final List<MethodAttributes> initializers = new ArrayList<>();
	private final List<MethodAttributes> validators = new ArrayList<>();

	UiMetadata(Injector injector, FieldProcessor fieldProcessor, IStringProcessor stringProcessor) {
		this.injector = injector;
		this.fieldProcessor = fieldProcessor;
		this.stringProcessor = stringProcessor;
	}

	public void addInitializer(MethodAttributes attributes) {
		if (attributes != null) {
			initializers.add(attributes);
		}
	}
	
	public void removeInitializer(MethodAttributes attributes) {
		initializers.remove(attributes);
	}

	public Iterable<MethodAttributes> getInitializers() {
		return initializers;
	}

	public void addValidator(MethodAttributes attributes) {
		if (attributes != null) {
			validators.add(attributes);
		}
	}
	
	public void removeValidator(MethodAttributes attributes) {
		validators.remove(attributes);
	}
	
	public Iterable<MethodAttributes> getValidators() {
		return validators;
	}

	public void addCategoryAttributes(CategoryAttributes attributes) {
		if (attributes != null) {
			categories.add(attributes);
			if (groups.size() > 0) {
				for (GroupAttributes ga : groups) {
					attributes.addGroupAttributes(ga);
				}
				groups.clear();
			}
		}
	}
	
	public void removeCategoryAttributes(CategoryAttributes attributes) {
		categories.remove(attributes);
	}
	
	public boolean hasCategoryAttributes(int categoryId) {
		for (CategoryAttributes ca : categories) {
			if (ca.getId() == categoryId) {
				return true;
			}
		}
		return false;
	}
	
	public Iterable<CategoryAttributes> getCategories() {
		return categories;
	}

	public boolean hasCategories() {
		return categories.size() > 0;
	}
	
	public Iterable<GroupAttributes> getGroups() {
		return groups;
	}

	public void addGroupAttributes(GroupAttributes attributes) {
		if (attributes != null) {
			if (categories.size() > 0) {
				categories.get(0).addGroupAttributes(attributes);
			} else {
				groups.add(attributes);
			}
		}
	}
	
	public void removeGroupAttributes(GroupAttributes attributes) {
		groups.remove(attributes);
	}
	
	public GroupAttributes findGroupAttributes(Predicate<GroupAttributes> predicate) {
		for (CategoryAttributes c : categories) {
			for (GroupAttributes ga : c) {
				if (predicate.test(ga)) {
					return ga;
				}
			}
		}
		for (GroupAttributes ga : groups) {
			if (predicate.test(ga)) {
				return ga;
			}
		}
		return null;
	}
	
	public void visitControlAttributes(Consumer<ControlAttributes> consumer) {
		for (CategoryAttributes c : categories) {
			for (GroupAttributes ga : c) {
				for (ControlAttributes ca : ga) {
					consumer.accept(ca);
				}
			}
		}
		for (GroupAttributes ga : groups) {
			for (ControlAttributes ca : ga) {
				consumer.accept(ca);
			}
		}
	}

	public ControlAttributes findControlAttributes(Predicate<ControlAttributes> predicate) {
		for (CategoryAttributes c : categories) {
			for (GroupAttributes ga : c) {
				for (ControlAttributes ca : ga) {
					if (predicate.test(ca)) {
						return ca;
					}
				}
			}
		}
		for (GroupAttributes ga : groups) {
			for (ControlAttributes ca : ga) {
				if (predicate.test(ca)) {
					return ca;
				}
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public <T extends IControl<?>> T findControl(Object data, Class<?> type, String fieldName) {
		IFieldReflection field = new ClassReflection<>(type).findField(fieldName);
		if (field != null) {
			ControlAttributes attrs = findControlAttributes(ca -> ca.getFieldReflection().equals(field) && ca.getData().equals(data));
			if (attrs != null) {
				return (T) attrs.getControl();
			}
		}
		return null;
	}
	
	public <T extends IControl<?>> T findControl(Object data, String fieldName) {
		return findControl(data, data.getClass(), fieldName);
	}
	
	public void commit() {
		visitControlAttributes(c -> commit(c.getData(), c.getFieldReflection(), c.getControl()));
	}

	private void commit(Object data, IFieldReflection field, IControl<?> control) {
		Object value = fieldProcessor.getFieldValue(control.getValue(), field);
		field.setValue(data, value);
	}

	public boolean isValid() {
		return findControlAttributes(ca -> !isControlValid(ca.getControl())) == null && validateMethods();		
	}
	
	private boolean isControlValid(IControl<?> control) {
		if (control instanceof IValidatableControl) {
			return ((IValidatableControl<?>) control).isControlValid();
		}
		return true;
	}

	void initialize() {
		initializeMethods();
		visitControlAttributes(ca -> init(ca.getControl(), ca.getFieldReflection()));
		validateMethods();	
	}

	private void init(IControl<?> control, IFieldReflection field) {
		validateControl(control, field);	
		control.addControlListener(c -> {
			validateControl(control, field);	
			validateMethods();
		});
	}

	private void validateControl(IControl<?> control, IFieldReflection field) {
		if (control instanceof IValidatableControl) {
			Object value = control.getValue();
			Annotation invalidAnnotation = fieldProcessor.validateValue(value, field);
			String msg = getValidationMessage(invalidAnnotation);
			((IValidatableControl<?>) control).setValidationMessage(msg);
		}
	}
	
	private boolean validateMethods() {
		boolean result = true;
		for (MethodAttributes ma : getValidators()) {
			Object valid = injector.invoke(ma.getData(), ma.getMethodReflection());
			if (valid instanceof Boolean) {
				result &= (Boolean) valid;
			}
		}
		return result;
	}
	
	private void initializeMethods() {
		for (MethodAttributes ma : getInitializers()) {
			injector.invoke(ma.getData(), ma.getMethodReflection());
		}
	}
	
	private String getValidationMessage(Annotation annotation) {
		if (annotation != null) {
			List<Object> params = new ArrayList<>();
			String message = null;
			for (IMethodReflection method : new ClassReflection<>(annotation.annotationType()).getMethods()) {
				Object value = method.invoke(annotation);
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
