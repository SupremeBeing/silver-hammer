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

import ru.silverhammer.model.CategoryModel;
import ru.silverhammer.model.UiModel;
import ru.silverhammer.processor.Categories.Category;
import ru.silverhammer.processor.Groups.Group;
import ru.silverhammer.conversion.IStringConverter;
import ru.silverhammer.reflection.ClassReflection;

public class CategoriesProcessor extends GroupsProcessor {

	public CategoriesProcessor(IStringConverter converter, UiModel model) {
		super(converter, model);
	}

	@Override
	public void process(Object data, ClassReflection<?> reflection, Annotation annotation) {
		if (annotation instanceof Categories) {
			Categories categories = (Categories) annotation;
			for (Category category : categories.value()) {
				process(data, reflection, category);
			}
		} else if (annotation instanceof Category) {
			Category category = (Category) annotation;
			CategoryModel categoryModel = createCategoryModel(category);
			model.getCategories().add(categoryModel);
			if (!model.getGroups().isEmpty()) {
				categoryModel.getGroups().addAll(model.getGroups());
				model.getGroups().clear();
			}
			for (Group g : category.groups()) {
				categoryModel.getGroups().add(createGroupModel(g));
			}
		}
	}

	private CategoryModel createCategoryModel(Category category) {
		CategoryModel result = new CategoryModel();
		result.setCaption(converter.getString(category.caption()));
		if (category.description().trim().length() > 0) {
			result.setDescription(converter.getString(category.description()));
		}
		result.setIconPath(category.icon());
		result.setMnemonic(category.mnemonic());
		return result;
	}
}
