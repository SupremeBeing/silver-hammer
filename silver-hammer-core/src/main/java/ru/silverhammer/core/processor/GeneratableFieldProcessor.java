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
import java.lang.reflect.Array;
import java.util.Collection;

import ru.silverhammer.core.metadata.UiMetadata;
import ru.silverhammer.injection.Inject;
import ru.silverhammer.injection.Injector;
import ru.silverhammer.reflection.AnnotatedReflection;
import ru.silverhammer.reflection.FieldReflection;
import ru.silverhammer.reflection.InstanceFieldReflection;
import ru.silverhammer.reflection.StaticFieldReflection;

public class GeneratableFieldProcessor extends Processor {

	public GeneratableFieldProcessor(@Inject Injector injector) {
		super(injector);
	}

	@Override
	public void process(UiMetadata metadata, Object data, AnnotatedReflection<?> member, Annotation annotation) {
		if (member instanceof FieldReflection) {
			FieldReflection field = (FieldReflection) member;
			Object val;
			if (member instanceof InstanceFieldReflection) {
				val = ((InstanceFieldReflection) member).getValue(data);
				if (field.getType().isArray()) {
					int length = Array.getLength(val);
					for (int i = 0; i < length; i++) {
						super.process(metadata, Array.get(val, i), field, annotation);
					}
				} else if (Collection.class.isAssignableFrom(field.getType())) {
					for (Object o : (Collection<?>) val) {
						super.process(metadata, o, field, annotation);
					}
				} else {
					super.process(metadata, val, field, annotation);
				}
			} else if (member instanceof StaticFieldReflection) {
				val = ((StaticFieldReflection) member).getValue();
				if (field.getType().isArray()) {
					int length = Array.getLength(val);
					for (int i = 0; i < length; i++) {
						super.process(metadata, Array.get(val, i), field, annotation);
					}
				} else if (Collection.class.isAssignableFrom(field.getType())) {
					for (Object o : (Collection<?>) val) {
						super.process(metadata, o, field, annotation);
					}
				} else {
					super.process(metadata, val, field, annotation);
				}
			}
		}
	}
}
