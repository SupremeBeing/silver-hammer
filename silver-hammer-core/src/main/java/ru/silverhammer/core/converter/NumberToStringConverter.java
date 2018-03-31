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

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;

import ru.silverhammer.common.Commons;
import ru.silverhammer.core.validator.annotation.NumberFormat;

public class NumberToStringConverter implements IConverter<Number, String, NumberFormat> {

	@Override
	public String convertForward(Number source, NumberFormat annotation) {
		if (source != null) {
			DecimalFormat fmt = new DecimalFormat(annotation.format());
			return fmt.format(source);
		}
		return "";
	}

	@Override
	public Number convertBackward(String destination, NumberFormat annotation) {
		Number result = null;
		if (destination != null && destination.trim().length() > 0) {
			try {
				DecimalFormat fmt = new DecimalFormat(annotation.format());
				if (annotation.type() == BigDecimal.class) {
					fmt.setParseBigDecimal(true);
				}
				result = fmt.parse(destination);
			} catch (ParseException e) {}
		}
		return Commons.convertNumber(result, annotation.type());
	}

}
