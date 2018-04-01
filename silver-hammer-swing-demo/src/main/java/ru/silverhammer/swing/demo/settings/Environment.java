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

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import ru.silverhammer.common.injection.Inject;
import ru.silverhammer.core.GroupId;
import ru.silverhammer.core.control.ICollectionControl;
import ru.silverhammer.core.control.IValidatableControl;
import ru.silverhammer.core.control.IValueTypeControl.ValueType;
import ru.silverhammer.core.control.annotation.Table;
import ru.silverhammer.core.converter.annotation.MapToList;
import ru.silverhammer.core.initializer.annotation.ControlProperties;
import ru.silverhammer.core.metadata.UiMetadata;
import ru.silverhammer.core.processor.annotation.InitializerMethod;
import ru.silverhammer.core.processor.annotation.ValidatorMethod;
import ru.silverhammer.core.processor.annotation.Categories.Category;
import ru.silverhammer.core.processor.annotation.Groups.Group;

@Category(caption = "Environment", mnemonic = 'e', groups = {
		@Group(value = "env")
})
public class Environment {

	@Table
	@GroupId("env")
	@ControlProperties(value = ValueType.Content, visibleRows = 10, captions = {"Key", "Value"})
	@MapToList(LinkedHashMap.class)
	private Map<String, Object> properties;
	
	@InitializerMethod
	private void initializeTable(@Inject UiMetadata metadata) {
		ICollectionControl<Object[], Object> table = metadata.findControl(this, "properties");
		table.addItem(new Object[] {"maven.test.skip", true});
		table.addItem(new Object[] {"JDK version", "1.8.0"});
		table.addItem(new Object[] {"timeout.interval", 100});
		table.addItem(new Object[] {"Current date", new Date()});
	}
	
	@ValidatorMethod
	private boolean validateTable(@Inject UiMetadata metadata) {
		ICollectionControl<Object[], Object> table = metadata.findControl(this, "properties");
		if (table instanceof IValidatableControl) {
			IValidatableControl<?> control = (IValidatableControl<?>) table;
			if (control.isControlValid() && !hasProperty(table, "required.key")) {
				control.setValidationMessage("Missing required property \"required.property\"");
			}
		}
		return true;
	}
	
	private boolean hasProperty(ICollectionControl<Object[], Object> table, String key) {
		for (int i = 0; i < table.getItemCount(); i++) {
			Object[] row = table.getItem(i);
			if (row.length > 0 && Objects.equals(row[0], key)) {
				return true;
			}
		}
		return false;
	}

}
