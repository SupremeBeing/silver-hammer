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
package ru.silverhammer.core.converter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import ru.silverhammer.core.converter.annotation.MapToList;
import ru.silverhammer.reflection.ClassReflection;

public class MapToListConverter implements IConverter<Map<?, ?>, Collection<Object[]>, MapToList> {

	@Override
	public Collection<Object[]> convertForward(Map<?, ?> source, MapToList annotation) {
		if (source != null) {
			Collection<Object[]> result = new ArrayList<>();
			for (Object key : source.keySet()) {
				Object value = source.get(key);
				result.add(new Object[] {key, value});
			}
			return result;
		}
		return null;
	}

	@Override
	public Map<?, ?> convertBackward(Collection<Object[]> destination, MapToList annotation) {
		if (destination != null) {
			@SuppressWarnings("rawtypes")
			ClassReflection<? extends Map> cr = new ClassReflection<>(annotation.value());
			@SuppressWarnings("unchecked")
			Map<Object, Object> result = cr.findConstructor().invoke();
			for (Object[] pair : destination) {
				result.put(pair[0], pair[1]);
			}
			return result;
		}
		return null;
	}
}
