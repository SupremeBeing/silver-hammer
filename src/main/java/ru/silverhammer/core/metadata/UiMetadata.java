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

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

import ru.silverhammer.common.Reflector;
import ru.silverhammer.core.control.IControl;

public class UiMetadata {

	private final List<CategoryAttributes> categories = new ArrayList<>();
	private final List<GroupAttributes> groups = new ArrayList<>();
	private final List<MethodAttributes> initializers = new ArrayList<>();
	private final List<MethodAttributes> validators = new ArrayList<>();

	private int generatedGroupId = -2;
	
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
	
	public int getNextGroupId() {
		return generatedGroupId--;
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
		Field field = Reflector.findField(type, fieldName);
		if (field != null) {
			ControlAttributes attrs = findControlAttributes((ca) -> ca.getField().equals(field) && ca.getData().equals(data));
			if (attrs != null) {
				return (T) attrs.getControl();
			}
		}
		return null;
	}
	
	public <T extends IControl<?>> T findControl(Object data, String fieldName) {
		return findControl(data, data.getClass(), fieldName);
	}
}
