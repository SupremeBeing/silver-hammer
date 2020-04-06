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
package ru.silverhammer.demo.settings;

import ru.sanatio.validator.NumberFormat;
import ru.silverhammer.control.*;
import ru.silverhammer.converter.NumberToString;
import ru.silverhammer.processor.Caption;
import ru.silverhammer.processor.GroupId;
import ru.silverhammer.Location;
import ru.silverhammer.VerticalAlignment;
import ru.silverhammer.converter.ArrayToCollection;
import ru.silverhammer.initializer.EnumerationItems;
import ru.silverhammer.initializer.FontFamilyItems;

import java.awt.*;

public class FontSettings {
	
	private enum Style {
		Bold {
			@Override
			public String toString() {
				return "<html><b>" + super.toString();
			}
		},
		Italic {
			@Override
			public String toString() {
				return "<html><i>" + super.toString();
			}
		},
		Strikeout {
			@Override
			public String toString() {
				return "<html><s>" + super.toString();
			}
		}
	}

	@Text
	@GroupId("font")
	@Caption("Line width:")
	@NumberFormat(format = "#0.##", message = "Line width should be in %s format")
	@NumberToString(type = double.class, format = "#0.##")
	private double lineWidth = 1.2;

	@SelectionList
	@GroupId("font")
	@Caption(value = "Family:", verticalAlignment = VerticalAlignment.Top)
	@FontFamilyItems
	private String family = "Tahoma";

	@Slider(min = 1, max = 72, minorTicks = 10, majorTicks = 25, ticks = false)
	@GroupId("font")
	@Caption(value = "Size:", verticalAlignment = VerticalAlignment.Top)
	private int size = 12;

	@CheckBoxGroup
	@GroupId("font")
	@Caption(value = "Style:", verticalAlignment = VerticalAlignment.Top)
	@EnumerationItems
	@ArrayToCollection(Style.class)
	private Style[] style = {Style.Bold, Style.Strikeout};
		
	@ColorChooser
	@GroupId("font")
	@Caption(value = "Color:", location = Location.Top)
	private int color = Color.GREEN.darker().darker().getRGB();
}
