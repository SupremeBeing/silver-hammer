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

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;

import ru.silverhammer.common.Reflector;
import ru.silverhammer.common.injection.Injector;
import ru.silverhammer.core.Categories.Category;
import ru.silverhammer.core.Groups.Group;
import ru.silverhammer.core.control.IControl;
import ru.silverhammer.core.metadata.CategoryAttributes;
import ru.silverhammer.core.metadata.ControlAttributes;
import ru.silverhammer.core.metadata.GroupAttributes;
import ru.silverhammer.core.metadata.MethodAttributes;
import ru.silverhammer.core.metadata.UiMetadata;
import ru.silverhammer.core.processor.IStringProcessor;

public class UiMetadataCollector {
	
	private final IStringProcessor stringProcessor;
	private final FieldProcessor fieldProcessor;
	private final Injector injector;

	public UiMetadataCollector(IStringProcessor stringProcessor, FieldProcessor fieldProcessor, Injector injector) {
		this.stringProcessor = stringProcessor;
		this.fieldProcessor = fieldProcessor;
		this.injector = injector;
	}

	public void collect(UiMetadata metadata, Object... data) {
		for (Object o : data) {
			if (o != null) {
				fillMetadata(o, metadata);
			}
		}
	}
	
	private void fillMetadata(Object data, UiMetadata metadata) {
		for (Class<?> cl : Reflector.getClassHierarchy(data.getClass())) {
			for (Category category : cl.getDeclaredAnnotationsByType(Category.class)) {
				if (!metadata.hasCategoryAttributes(category.hashCode())) {
					metadata.addCategoryAttributes(createCategoryAttributes(category));
				}
			}
			for (Group group : cl.getDeclaredAnnotationsByType(Group.class)) {
				if (metadata.findGroupAttributes((g) -> g.getId() == group.id()) == null) {
					metadata.addGroupAttributes(createGroupAttributes(group));
				}
			}
			for (Method method : cl.getDeclaredMethods()) {
				if (method.getAnnotation(InitializerMarker.class) != null) {
					metadata.addInitializer(new MethodAttributes(data, method));
				}
				if (method.getAnnotation(ValidatorMarker.class) != null) {
					metadata.addValidator(new MethodAttributes(data, method));
				}
			}
			GroupAttributes prevGroup = null;
			for (Field field : cl.getDeclaredFields()) {
				if (field.getAnnotation(GeneratableType.class) != null) {
					Object val = Reflector.getFieldValue(data, field);
					if (field.getType().isArray()) {
						int length = Array.getLength(val);
						for (int i = 0; i < length; i++) {
							fillMetadata(Array.get(val, i), metadata);
						}
					} else if (Collection.class.isAssignableFrom(field.getType())) {
						for (Object o : (Collection<?>) val) {
							fillMetadata(o, metadata);
						}
					} else {
						fillMetadata(val, metadata);
					}
				} else {
					Class<? extends IControl<?>> controlClass = fieldProcessor.getControlClass(field);
					if (controlClass != null) {
						IControl<?> control = injector.instantiate(controlClass);
						prevGroup = addControlAttributes(metadata, field.getAnnotation(GroupId.class), createControlAttributes(control, data, field), prevGroup);
					}
				}
			}
		}
	}
	
	private GroupAttributes addControlAttributes(UiMetadata metadata, GroupId gi, ControlAttributes attributes, GroupAttributes prevGroup) {
		if (gi == null) {
			if (prevGroup != null && prevGroup.getId() < 0) {
				prevGroup.addControlAttributes(attributes);
				return prevGroup;
			} else {
				GroupAttributes group = new GroupAttributes(metadata.getNextGroupId());
				metadata.addGroupAttributes(group);
				group.addControlAttributes(attributes);
				return group;
			}
		} else {
			GroupAttributes group = metadata.findGroupAttributes((g) -> g.getId() == gi.value());
			if (group == null) {
				group = new GroupAttributes(gi.value());
				metadata.addGroupAttributes(group);
			}
			group.addControlAttributes(attributes);
			return group;
		}
	}
	
	private CategoryAttributes createCategoryAttributes(Category category) {
		CategoryAttributes result = new CategoryAttributes(category.hashCode());
		result.setCaption(stringProcessor.getString(category.caption()));
		if (category.description().trim().length() > 0) {
			result.setDescription(stringProcessor.getString(category.description()));
		}
		result.setIconPath(category.icon());
		result.setMnemonic(category.mnemonic());
		for (Group g : category.groups()) {
			result.addGroupAttributes(createGroupAttributes(g));
		}
		return result;
	}
	
	private GroupAttributes createGroupAttributes(Group group) {
		GroupAttributes result = new GroupAttributes(group.id());
		result.setCaption(group.caption().trim().length() > 0 ? stringProcessor.getString(group.caption()) : null);
		return result;
	}
	
	private ControlAttributes createControlAttributes(IControl<?> control, Object data, Field field) {
		ControlAttributes result = new ControlAttributes(control, data, field);
		Caption caption = field.getAnnotation(Caption.class);
		Description description = field.getAnnotation(Description.class);
		if (caption != null) {
			result.setCaption(stringProcessor.getString(caption.value()));
			result.setCaptionLocation(caption.location());
			result.setHorizontalAlignment(caption.horizontalAlignment());
			result.setVerticalAlignment(caption.verticalAlignment());
		}
		if (description != null) {
			result.setDescription(stringProcessor.getString(description.value()));
		}
		return result;
	}
}
