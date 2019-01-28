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

import ru.silverhammer.Location;
import ru.silverhammer.VerticalAlignment;
import ru.silverhammer.model.UiModel;
import ru.silverhammer.control.ICollectionControl;
import ru.silverhammer.control.ISelectionControl;
import ru.silverhammer.control.ContentTable;
import ru.silverhammer.control.Text;
import ru.silverhammer.control.Tree;
import ru.silverhammer.converter.MapToCollection;
import ru.silverhammer.decorator.ButtonBar;
import ru.silverhammer.decorator.ButtonBar.Button;
import ru.silverhammer.initializer.FileTreeItems;
import ru.silverhammer.processor.Processor;
import ru.silverhammer.processor.Caption;
import ru.silverhammer.processor.Structure.Category;
import ru.silverhammer.processor.GroupId;
import ru.silverhammer.processor.Structure.Group;
import ru.silverhammer.processor.Initializer;
import ru.silverhammer.processor.Validator;
import ru.silverhammer.resolver.IControlResolver;
import ru.silverhammer.swing.SwingUiBuilder;
import ru.silverhammer.validator.MinSize;

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
	
	@ContentTable(captions = {"Key", "Value"}, visibleRows = 10)
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
	@Initializer
	private void initializeTable(UiModel metadata) {
		ICollectionControl<Object, ?, Object[]> table = metadata.findControl(this, "properties");
		table.getCollection().add(new Object[] {"maven.test.skip", true});
		table.getCollection().add(new Object[] {"JDK version", "1.8.0"});
		table.getCollection().add(new Object[] {"timeout.interval", 100});
		table.getCollection().add(new Object[] {"Current date", new Date()});
	}

	@SuppressWarnings("unused")
	@Validator
	private void validateTable(UiModel metadata) {
		ICollectionControl<Object, ?, Object[]> table = metadata.findControl(this, "properties");
		if (table.isControlValid() && !hasProperty(table, "required.key")) {
			table.setValidationMessage("Missing required property \"required.key\"");
		}
	}
	
	private boolean hasProperty(ICollectionControl<Object, ?, Object[]> table, String key) {
		for (int i = 0; i < table.getCollection().size(); i++) {
			Object[] row = table.getCollection().get(i);
			if (row.length > 0 && Objects.equals(row[0], key)) {
				return true;
			}
		}
		return false;
	}

	@SuppressWarnings("unused")
	private void addPressed(UiModel metadata, IControlResolver resolver) {
		ICollectionControl<Object, ?, Object[]> table = metadata.findControl(this, "properties");
		KeyValue val = new KeyValue();
		Processor processor = new Processor(resolver);
		SwingUiBuilder builder = new SwingUiBuilder("Add property");
		if (builder.showUi(processor.process(val))) {
			table.getCollection().add(new Object[] {val.key, val.value});
		}
	}

	@SuppressWarnings("unused")
	private void deletePressed(UiModel metadata) {
		ISelectionControl<?, ?, Object[]> sc = metadata.findControl(this, "properties");
		if (sc.getSelection().size() > 0) {
			ICollectionControl<?, ?, Object[]> cc = metadata.findControl(this, "properties");
			Object[] selected = sc.getSelection().get(0);
			for (int i = 0; i < cc.getCollection().size(); i++) {
				if (Objects.equals(cc.getCollection().get(i), selected)) {
					cc.getCollection().remove(i);
					break;
				}
			}
		}
	}

	@SuppressWarnings("unused")
	private boolean updateDelete(UiModel metadata) {
		if (metadata != null) {
			ISelectionControl<?, ?, ?> control = metadata.findControl(this, "properties");
			return control.getSelection().size() > 0;
		}
		return false;
	}
}
