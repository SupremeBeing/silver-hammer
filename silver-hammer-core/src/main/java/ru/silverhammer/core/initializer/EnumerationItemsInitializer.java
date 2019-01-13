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
package ru.silverhammer.core.initializer;

import ru.silverhammer.core.control.ICollectionControl;
import ru.silverhammer.core.initializer.annotation.EnumerationItems;
import ru.silverhammer.reflection.ClassReflection;
import ru.silverhammer.reflection.IFieldReflection;

import java.util.Collection;
import java.util.List;

public class EnumerationItemsInitializer implements IInitializer<ICollectionControl<Object, ?, ?>, EnumerationItems> {

	@Override
	public void init(ICollectionControl<Object, ?, ?> control, EnumerationItems annotation, Object data, IFieldReflection field) {
		Class<?> cl;
		if (annotation.value() == Void.class) {
			Class<?> fieldClass = field.getType();
			if (fieldClass.isArray()) {
				cl = fieldClass.getComponentType();
			} else if (Collection.class.isAssignableFrom(fieldClass)) {
				List<ClassReflection<?>> cls = field.getGenericClasses();
				cl = cls.isEmpty() ? Object.class : cls.get(0).getType();
			} else {
				cl = fieldClass;
			}
		} else {
			cl = annotation.value();
		}
		for (Object e : cl.getEnumConstants()) {
			control.getCollection().add(e);
		}
	}
}
