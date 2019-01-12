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
package ru.silverhammer.swing.control;

import ru.silverhammer.core.control.annotation.RadioButtonGroup;

import javax.swing.*;
import java.util.*;

public class RadioButtonGroupControl extends ButtonGroupControl<RadioButtonGroup> {

	private static final long serialVersionUID = 7058197271259148125L;

	@Override
	protected AbstractButton createButton() {
		return new JRadioButton();
	}

	@Override
	protected AbstractButton createButton(Object item) {
		AbstractButton button = createButton();
		button.setText(item.toString());
		button.addActionListener(l -> {
			clearSelection(button);
			fireValueChanged();
		});
		return button;
	}

	private void clearSelection(AbstractButton button) {
		for (Object item : data) {
			AbstractButton btn = getButton(item);
			btn.setSelected(Objects.equals(button, btn));
		}
	}

	@Override
	public Object getValue() {
		for (Object item : data) {
			AbstractButton btn = getButton(item);
			if (btn.isSelected()) {
				return item;
			}
		}
		return null;
	}

	@Override
	public void setValue(Object value) {
		AbstractButton button = getButton(value);
		if (button != null) {
			button.setSelected(true);
			fireValueChanged();
		}
	}
}
