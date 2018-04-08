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
package ru.silverhammer.common;

import java.math.BigDecimal;
import java.math.BigInteger;

public final class Commons {

	private Commons() {}

	public static Number convertNumber(Number value, Class<?> target) {
		if (value != null) {
			if (value.getClass() != target) {
				if (target == Byte.class || target == byte.class) {
					return value.byteValue();
				} else if (target == Short.class || target == short.class) {
					return value.shortValue();
				} else if (target == Integer.class || target == int.class) {
					return value.intValue();
				} else if (target == Long.class || target == long.class) {
					return value.longValue();
				} else if (target == Float.class || target == float.class) {
					return value.floatValue();
				} else if (target == Double.class || target == double.class) {
					return value.doubleValue();
				} else if (target == BigInteger.class) {
					return new BigInteger(Long.toString(value.longValue()));
				} else if (target == BigDecimal.class) {
					return value instanceof BigInteger ? new BigDecimal((BigInteger) value) : new BigDecimal(value.doubleValue());
				}
			}
		} else if (target.isPrimitive()) {
			if (target == byte.class) {
				return (byte) 0;
			} else if (target == short.class) {
				return (short) 0;
			} else if (target == int.class) {
				return (int) 0;
			} else if (target == long.class) {
				return (long) 0;
			} else if (target == float.class) {
				return (float) 0;
			} else if (target == double.class) {
				return (double) 0;
			}
		}
		return value;
	}
}
