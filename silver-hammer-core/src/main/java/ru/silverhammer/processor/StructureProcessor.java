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
import ru.silverhammer.model.GroupModel;
import ru.silverhammer.model.UiModel;
import ru.silverhammer.processor.Structure.Category;
import ru.silverhammer.processor.Structure.Group;
import ru.silverhammer.conversion.IStringConverter;
import ru.silverhammer.reflection.ClassReflection;

// TODO: consider adding groups based on their occurrence in class fields
public class StructureProcessor implements IProcessor<ClassReflection<?>, Annotation>  {

	private final IStringConverter converter;
	private final UiModel model;

	public StructureProcessor(IStringConverter converter, UiModel model) {
		this.converter = converter;
		this.model = model;
	}

	@Override
	public void process(Object data, ClassReflection<?> reflection, Annotation annotation) {
		if (annotation instanceof Structure) {
			Structure structure = (Structure) annotation;
			for (Category category : structure.value()) {
				process(data, reflection, category);
			}
		} else if (annotation instanceof Category) {
			Category category = (Category) annotation;
			CategoryModel categoryModel = model.findCategoryModel(converter.getString(category.caption()));
			if (categoryModel == null) {
				categoryModel = createCategoryModel(category);
				model.getCategories().add(categoryModel);
			}
			for (Group g : category.groups()) {
				if (model.findGroupModel(g.value()) == null) {
					GroupModel groupModel = createGroupModel(g);
					categoryModel.getGroups().add(groupModel);
				}
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

	private GroupModel createGroupModel(Group group) {
		GroupModel result = new GroupModel(group.value());
		result.setCaption(group.caption().trim().length() > 0 ? converter.getString(group.caption()) : null);
		return result;
	}

}
