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
package ru.silverhammer.swing.demo;

import ru.silverhammer.core.processor.annotation.GeneratableField;

import java.util.LinkedHashMap;
import java.util.Map;

import ru.silverhammer.core.GroupId;
import ru.silverhammer.core.control.IValueTypeControl.ValueType;
import ru.silverhammer.core.control.annotation.RadioGroup;
import ru.silverhammer.core.control.annotation.Table;
import ru.silverhammer.core.converter.annotation.MapToList;
import ru.silverhammer.core.initializer.annotation.ControlProperties;
import ru.silverhammer.core.initializer.annotation.StringItems;
import ru.silverhammer.core.processor.annotation.Categories.Category;
import ru.silverhammer.core.processor.annotation.Groups.Group;

@Category(caption = "Properties", mnemonic = 'p', groups = {
		@Group(value = "props")
})
@Category(caption = "Settings", mnemonic = 's', groups = {
		@Group(value = "lang", caption = "Programming language"),
		@Group(value = "font", caption = "Font")
})
public class Settings {

	@RadioGroup
	@GroupId("lang")
	@StringItems({"Java", "C#", "C++", "Python", "JavaScript", "PHP"})
	private String language = "Java";

	@GeneratableField
	private FontSettings fontSettings = new FontSettings();
	
	@Table
	@GroupId("props")
	@ControlProperties(value = ValueType.Content, visibleRows = 10, captions = {"Key", "Value"})
	@MapToList(LinkedHashMap.class)
	private Map<String, Object> properties = new LinkedHashMap<String, Object>() {
		private static final long serialVersionUID = 7885610738307123806L;
		{
			put("maven.test.skip", true);
			put("JDK version", "1.8.0");
			put("timeout.interval", 100);
		}
	};
}
