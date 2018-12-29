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
package ru.silverhammer.swing.initializer;

import java.awt.BorderLayout;
import java.awt.Insets;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

import ru.silverhammer.core.initializer.IInitializer;
import ru.silverhammer.core.string.IStringProcessor;
import ru.silverhammer.injection.Inject;
import ru.silverhammer.reflection.IFieldReflection;
import ru.silverhammer.swing.control.Control;
import ru.silverhammer.swing.initializer.annotation.FileChooserAddon;

public class FileChooserAddonInitializer implements IInitializer<Control<String, JTextField>, FileChooserAddon> {

	private final IStringProcessor processor;

	public FileChooserAddonInitializer(@Inject IStringProcessor processor) {
		this.processor = processor;
	}

	@Override
	public void init(Control<String, JTextField> control, FileChooserAddon annotation, Object data, IFieldReflection field) {
		JButton button = new JButton(processor.getString(annotation.buttonCaption()));
		button.setMargin(new Insets(0, 5, 0, 5));
		control.add(button, BorderLayout.EAST);
		button.addActionListener(e -> showDialog(annotation, control));
	}

	private void showDialog(FileChooserAddon annotation, Control<String, JTextField> control) {
		JFileChooser dlg = new JFileChooser();
		dlg.setFileSelectionMode(annotation.allowDirectories() ? JFileChooser.FILES_AND_DIRECTORIES : JFileChooser.FILES_ONLY);
		dlg.setDragEnabled(false);
		dlg.setMultiSelectionEnabled(false);

		if (annotation.filters().length > 0) {
			dlg.setAcceptAllFileFilterUsed(false);
			for (String s : annotation.filters()) {
				String[] pairs = s.split("\\|");
				if (pairs.length == 2) {
					String[] extensions = pairs[1].split(",");
					for (int i = 0; i < extensions.length; i++) {
						extensions[i] = extensions[i].trim();
					}
					dlg.addChoosableFileFilter(new FileNameExtensionFilter(pairs[0], extensions));
				}
			}
		} else {
			dlg.setAcceptAllFileFilterUsed(true);
		}
		
		if (control.getValue() != null) {
			dlg.setCurrentDirectory(new File(control.getValue()).getParentFile());
		} else if (annotation.initialDirectory().length() > 0) {
			dlg.setCurrentDirectory(new File(annotation.initialDirectory()));
		}

		String approveCaption = processor.getString(annotation.approveCaption());
		if (dlg.showDialog(control, approveCaption) == JFileChooser.APPROVE_OPTION) {
			String file = dlg.getSelectedFile().getPath();
			if (dlg.getFileFilter() instanceof FileNameExtensionFilter) {
				file = checkExtension(file, (FileNameExtensionFilter) dlg.getFileFilter());
			}
			control.setValue(file);
		}
	}
	
	private String checkExtension(String file, FileNameExtensionFilter filter) {
		String[] extensions = filter.getExtensions();
		if (extensions.length > 0) {
			String ext = "." + extensions[0];
			if (!file.endsWith(ext)) {
				return file + ext;
			}
		}
		return file;
	}
}
