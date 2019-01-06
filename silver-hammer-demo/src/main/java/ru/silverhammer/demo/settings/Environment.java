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

import ru.silverhammer.core.*;
import ru.silverhammer.core.control.ICollectionControl;
import ru.silverhammer.core.control.ISelectionControl;
import ru.silverhammer.core.control.IValidatableControl;
import ru.silverhammer.core.control.ValueType;
import ru.silverhammer.core.control.annotation.Table;
import ru.silverhammer.core.control.annotation.Text;
import ru.silverhammer.core.control.annotation.Tree;
import ru.silverhammer.core.converter.annotation.MapToCollection;
import ru.silverhammer.core.decorator.annotation.ButtonBar;
import ru.silverhammer.core.decorator.annotation.ButtonBar.Button;
import ru.silverhammer.core.initializer.annotation.FileTreeItems;
import ru.silverhammer.core.metadata.UiMetadata;
import ru.silverhammer.core.processor.annotation.Categories.Category;
import ru.silverhammer.core.processor.annotation.Groups.Group;
import ru.silverhammer.core.processor.annotation.InitializerMethod;
import ru.silverhammer.core.processor.annotation.ValidatorMethod;
import ru.silverhammer.core.validator.annotation.MinSize;

import java.io.File;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

@Category(caption = "Environment", mnemonic = 'e', groups = {
		@Group(value = "env")
})
public class Environment {
	
	private static class KeyValue {
		
		@Text
		@Caption("Key:")
		@MinSize(value = 1, message = "Key should be specified")
		private String key;

		@Text
		@Caption("Value:")
		private String value;

	}
	
	@Table(captions = {"Key", "Value"}, value = ValueType.Content, visibleRows = 10)
	@GroupId("env")
	@MapToCollection(LinkedHashMap.class)
	@ButtonBar(value = {
			@Button(caption = "Add", icon = "/add.png", pressedMethod = "addPressed"),
			@Button(caption = "Delete", icon = "/delete.png", pressedMethod = "deletePressed", enabledMethod = "updateDelete")
	}, location = Location.Right, verticalAlignment = VerticalAlignment.Top)
	private Map<String, Object> properties;
	
	@Tree(visibleRows = 10)
	@GroupId("env")
	@Caption(value = "Current directory:", location = Location.Top)
	@FileTreeItems(".")
	private File root;

	@SuppressWarnings("unused")
	@InitializerMethod
	private void initializeTable(UiMetadata metadata) {
		ICollectionControl<Object[], Object, ?> table = metadata.findControl(this, "properties");
		table.addItem(new Object[] {"maven.test.skip", true});
		table.addItem(new Object[] {"JDK version", "1.8.0"});
		table.addItem(new Object[] {"timeout.interval", 100});
		table.addItem(new Object[] {"Current date", new Date()});
	}

	@SuppressWarnings("unused")
	@ValidatorMethod
	private void validateTable(UiMetadata metadata) {
		ICollectionControl<Object[], Object, ?> table = metadata.findControl(this, "properties");
		if (table instanceof IValidatableControl) {
			IValidatableControl<?, ?> control = (IValidatableControl<?, ?>) table;
			if (control.isControlValid() && !hasProperty(table, "required.key")) {
				control.setValidationMessage("Missing required property \"required.key\"");
			}
		}
	}
	
	private boolean hasProperty(ICollectionControl<Object[], Object, ?> table, String key) {
		for (int i = 0; i < table.getItemCount(); i++) {
			Object[] row = table.getItem(i);
			if (row.length > 0 && Objects.equals(row[0], key)) {
				return true;
			}
		}
		return false;
	}

	@SuppressWarnings("unused")
	private void addPressed(UiMetadata metadata, IUiBuilder<?> builder) {
		ICollectionControl<Object[], Object, ?> table = metadata.findControl(this, "properties");
		KeyValue val = new KeyValue();
		if (builder.showDialog("Add property", val)) {
			table.addItem(new Object[] {val.key, val.value});
		}
	}

	@SuppressWarnings("unused")
	private void deletePressed(UiMetadata metadata) {
		ISelectionControl<Object[], ?, ?> sc = metadata.findControl(this, "properties");
		Object[] sel = sc.getSingleSelection();
		if (sel != null) {
			ICollectionControl<Object[], ?, ?> cc = metadata.findControl(this, "properties");
			cc.removeItem(sel);
		}
	}

	@SuppressWarnings("unused")
	private boolean updateDelete(UiMetadata metadata) {
		if (metadata != null) {
			ISelectionControl<?, ?, ?> control = metadata.findControl(this, "properties");
			return control.getSingleSelection() != null;
		}
		return false;
	}
}
