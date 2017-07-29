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

import ru.silverhammer.core.control.annotation.CheckBox;
import ru.silverhammer.core.control.annotation.CheckBoxGroup;
import ru.silverhammer.core.control.annotation.ColorChooser;
import ru.silverhammer.core.control.annotation.ComboBox;
import ru.silverhammer.core.control.annotation.Label;
import ru.silverhammer.core.control.annotation.List;
import ru.silverhammer.core.control.annotation.Password;
import ru.silverhammer.core.control.annotation.RadioGroup;
import ru.silverhammer.core.control.annotation.Slider;
import ru.silverhammer.core.control.annotation.Table;
import ru.silverhammer.core.control.annotation.Text;
import ru.silverhammer.core.control.annotation.TextArea;
import ru.silverhammer.core.control.annotation.Tree;
import ru.silverhammer.core.resolver.ControlResolver;
import ru.silverhammer.swing.control.CheckBoxControl;
import ru.silverhammer.swing.control.CheckBoxGroupControl;
import ru.silverhammer.swing.control.ColorChooserControl;
import ru.silverhammer.swing.control.ComboBoxControl;
import ru.silverhammer.swing.control.LabelControl;
import ru.silverhammer.swing.control.ListControl;
import ru.silverhammer.swing.control.PasswordControl;
import ru.silverhammer.swing.control.RadioGroupControl;
import ru.silverhammer.swing.control.SliderControl;
import ru.silverhammer.swing.control.TableControl;
import ru.silverhammer.swing.control.TextAreaControl;
import ru.silverhammer.swing.control.TextControl;
import ru.silverhammer.swing.control.TreeControl;

public class SwingControlResolver extends ControlResolver {

	public SwingControlResolver() {
		bind(CheckBox.class, CheckBoxControl.class);
		bind(Text.class, TextControl.class);
		bind(ComboBox.class, ComboBoxControl.class);
		bind(ColorChooser.class, ColorChooserControl.class);
		bind(TextArea.class, TextAreaControl.class);
		bind(RadioGroup.class, RadioGroupControl.class);
		bind(Password.class, PasswordControl.class);
		bind(Slider.class, SliderControl.class);
		bind(Label.class, LabelControl.class);
		bind(Tree.class, TreeControl.class);
		bind(Table.class, TableControl.class);
		bind(List.class, ListControl.class);
		bind(CheckBoxGroup.class, CheckBoxGroupControl.class);
	}
}
