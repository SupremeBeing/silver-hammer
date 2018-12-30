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
import java.util.Objects;

import ru.silverhammer.core.metadata.CategoryAttributes;
import ru.silverhammer.core.metadata.GroupAttributes;
import ru.silverhammer.core.metadata.UiMetadata;
import ru.silverhammer.core.processor.annotation.Categories;
import ru.silverhammer.core.processor.annotation.Groups;
import ru.silverhammer.core.processor.annotation.Categories.Category;
import ru.silverhammer.core.processor.annotation.Groups.Group;
import ru.silverhammer.core.string.IStringProcessor;
import ru.silverhammer.reflection.IReflection;

public class StructureClassProcessor implements IProcessor {

	private final IStringProcessor stringProcessor;
	
	public StructureClassProcessor(IStringProcessor stringProcessor) {
		this.stringProcessor = stringProcessor;
	}

	@Override
	public void process(UiMetadata metadata, Object data, IReflection reflection, Annotation annotation) {
		if (annotation instanceof Groups) {
			Groups groups = (Groups) annotation;
			for (Group group : groups.value()) {
				if (metadata.findGroupAttributes(g -> Objects.equals(g.getId(), group.value())) == null) {
					metadata.addGroupAttributes(createGroupAttributes(group));
				}
			}
		} else if (annotation instanceof Group) {
			Group group = (Group) annotation;
			if (metadata.findGroupAttributes(g -> Objects.equals(g.getId(), group.value())) == null) {
				metadata.addGroupAttributes(createGroupAttributes(group));
			}
		} else if (annotation instanceof Categories) {
			Categories categories = (Categories) annotation;
			for (Category category : categories.value()) {
				if (!metadata.hasCategoryAttributes(category.hashCode())) {
					metadata.addCategoryAttributes(createCategoryAttributes(category));
				}
			}
		} else if (annotation instanceof Category) {
			Category category = (Category) annotation;
			if (!metadata.hasCategoryAttributes(category.hashCode())) {
				metadata.addCategoryAttributes(createCategoryAttributes(category));
			}
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
		GroupAttributes result = new GroupAttributes(group.value());
		result.setCaption(group.caption().trim().length() > 0 ? stringProcessor.getString(group.caption()) : null);
		return result;
	}
}
