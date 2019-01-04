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
package ru.silverhammer.swing.demo.settings;

import java.awt.Color;

import ru.silverhammer.core.Caption;
import ru.silverhammer.core.GroupId;
import ru.silverhammer.core.Location;
import ru.silverhammer.core.VerticalAlignment;
import ru.silverhammer.core.control.annotation.*;
import ru.silverhammer.core.converter.annotation.ArrayToList;
import ru.silverhammer.core.initializer.annotation.EnumerationItems;
import ru.silverhammer.core.validator.annotation.NumberFormat;
import ru.silverhammer.swing.initializer.annotation.FontFamilyItems;

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
	@NumberFormat(type = double.class, format = "#0.##", message = "Line width should be in %s format")
	private double lineWidth = 1.2;

	@List
	@GroupId("font")
	@Caption(value = "Family:", verticalAlignment = VerticalAlignment.Top)
	@FontFamilyItems
	private String family = "Tahoma";

	@Slider(min = 1, max = 72, minorTicks = 10, majorTicks = 25, ticks = false)
	@GroupId("font")
	@Caption(value = "Size:", verticalAlignment = VerticalAlignment.Top)
	private int size = 12;

	@ButtonGroup
	@GroupId("font")
	@Caption(value = "Style:", verticalAlignment = VerticalAlignment.Top)
	@EnumerationItems(Style.class)
	@ArrayToList(Style.class)
	private Style[] style = {Style.Bold, Style.Strikeout};
		
	@ColorChooser
	@GroupId("font")
	@Caption(value = "Color:", location = Location.Top)
	private int color = Color.GREEN.darker().darker().getRGB();
}
