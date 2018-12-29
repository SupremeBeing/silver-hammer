/*
 * Copyright (c) 2018, Dmitriy Shchekotin
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
package ru.silverhammer.reflection;

import java.util.Objects;

public enum Primitive {
	
	Boolean(boolean.class, Boolean.class),
	Byte(byte.class, Byte.class),
	Char(char.class, Character.class),
	Short(short.class, Short.class),
	Int(int.class, Integer.class),
	Long(long.class, Long.class),
	Float(float.class, Float.class),
	Double(double.class, Double.class),
	Void(void.class, Void.class);

	public static Primitive findByPrimitiveType(Class<?> primitiveType) {
		for (Primitive primitive : values()) {
			if (Objects.equals(primitive.getPrimitiveType(), primitiveType)) {
				return primitive;
			}
		}
		return null;
	}

	public static Primitive findByBoxedType(Class<?> boxedType) {
		for (Primitive primitive : values()) {
			if (Objects.equals(primitive.getBoxedType(), boxedType)) {
				return primitive;
			}
		}
		return null;
	}

	public static boolean exists(Class<?> primitiveType, Class<?> boxedType) {
		Primitive primitive = findByPrimitiveType(primitiveType);
		return primitive != null && Objects.equals(primitive.getBoxedType(), boxedType);
	}
	
	private final Class<?> primitiveType;
	private final Class<?> boxedType;
	
	Primitive(Class<?> primitiveType, Class<?> boxedType) {
		this.primitiveType = primitiveType;
		this.boxedType = boxedType;
	}

	public Class<?> getPrimitiveType() {
		return primitiveType;
	}

	public Class<?> getBoxedType() {
		return boxedType;
	}	
}
