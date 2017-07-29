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
package ru.silverhammer.swing.control;

import javax.swing.ButtonGroup;
import javax.swing.JRadioButton;

public class RadioGroupControl extends ButtonGroupControl<Object, JRadioButton> {

	private static final long serialVersionUID = 1787773093280522547L;

	private final ButtonGroup group = new ButtonGroup();

	@Override
	protected JRadioButton createButton() {
		return new JRadioButton();
	}

	@Override
	public Object getValue() {
		for (Object item : getData()) {
			JRadioButton btn = getButton(item);
			if (btn.isSelected()) {
				return item;
			}
		}
		return null;
	}

	@Override
	public void setValue(Object value) {
		JRadioButton button = getButton(value);
		if (button != null) {
			button.setSelected(true);
			fireValueChanged();
		}
	}

	@Override
	public void removeItem(Object item) {
		group.remove(getButton(item));
		super.removeItem(item);
	}

	@Override
	public void clearItems() {
		for (Object item : getData()) {
			group.remove(getButton(item));
		}
		super.clearItems();
	}
	
	@Override
	protected JRadioButton createButton(Object item) {
		JRadioButton result = super.createButton(item);
		group.add(result);
		return result;
	}
	
	@Override
	protected void rebuild() {
		for (Object item : getData()) {
			group.remove(getButton(item));
		}
		super.rebuild();
	}
}
