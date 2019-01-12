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
package ru.silverhammer.swing;

import ru.silverhammer.core.control.annotation.*;
import ru.silverhammer.core.decorator.annotation.ButtonBar;
import ru.silverhammer.core.decorator.annotation.FileChooser;
import ru.silverhammer.core.resolver.ControlResolver;
import ru.silverhammer.swing.control.*;
import ru.silverhammer.swing.decorator.ButtonBarDecorator;
import ru.silverhammer.swing.decorator.FileChooserDecorator;

public class SwingControlResolver extends ControlResolver {

	public SwingControlResolver() {
		bindControl(CheckBox.class, CheckBoxControl.class);
		bindControl(Text.class, TextControl.class);
		bindControl(ComboBox.class, ComboBoxControl.class);
		bindControl(ColorChooser.class, ColorChooserControl.class);
		bindControl(TextArea.class, TextAreaControl.class);
		bindControl(CheckBoxGroup.class, CheckBoxGroupControl.class);
		bindControl(RadioButtonGroup.class, RadioButtonGroupControl.class);
		bindControl(Password.class, PasswordControl.class);
		bindControl(Slider.class, SliderControl.class);
		bindControl(Label.class, LabelControl.class);
		bindControl(Tree.class, TreeControl.class);
		bindControl(SelectionTable.class, SelectionTableControl.class);
		bindControl(ContentTable.class, ContentTableControl.class);
		bindControl(SelectionList.class, SelectionListControl.class);
		bindControl(ContentList.class, ContentListControl.class);

		bindDecorator(ButtonBar.class, ButtonBarDecorator.class);
		bindDecorator(FileChooser.class, FileChooserDecorator.class);
	}
}
