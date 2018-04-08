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

import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractButton;
import javax.swing.JPanel;

import ru.silverhammer.core.control.ICollectionControl;

public abstract class ButtonGroupControl<Value, Button extends AbstractButton>
	extends ValidatableControl<Value, JPanel> implements ICollectionControl<Object, Value> {

	private static final long serialVersionUID = 7058197271259148125L;

	private final List<Object> data = new ArrayList<>(); 
	private final Map<Object, Button> buttons = new HashMap<>();

	public ButtonGroupControl() {
		super(false);
		setNormalBackground(createButton().getBackground());
	}
	
	@Override
	protected JPanel createComponent() {
		return new JPanel(new GridLayout(0, 1, 0, 0));
	}

	@Override
	public void setValidationMessage(String message) {
		for (Button c : buttons.values()) {
			c.setToolTipText(message);
			c.setBackground(message == null ? getNormalBackground() : getInvalidBackground());
		}
	}

	protected abstract Button createButton();
	
	protected Button createButton(Object item) {
		Button button = createButton();
		button.setText(item.toString());
		button.addActionListener(l -> fireValueChanged());
		return button;
	}
	
	protected Button getButton(Object item) {
		return buttons.get(item);
	}
	
	protected Iterable<Object> getData() {
		return data;
	}
	
	@Override
	public void setItem(int i, Object item) {
		if (item != null) {
			Object oldItem = data.get(i);
			Button button = buttons.get(oldItem);
			button.setText(item.toString());
			data.set(i, item);
			buttons.remove(oldItem);
			buttons.put(item, button);
			if (button.isSelected()) {
				fireValueChanged();
			}
		}
	}

	@Override
	public void clearItems() {
		getComponent().removeAll();
		buttons.clear();
		data.clear();
		fireValueChanged();
	}

	@Override
	public void addItem(Object item) {
		if (item != null) {
			Button button = createButton(item);
			buttons.put(item, button);
			data.add(item);
			getComponent().add(button);
		}
	}

	@Override
	public void removeItem(Object item) {
		Button button = getButton(item);
		if (button != null) {
			getComponent().remove(button);
			buttons.remove(item);
			data.remove(item);
			if (button.isSelected()) {
				fireValueChanged();
			}
		}
	}

	@Override
	public void addItem(int i, Object item) {
		if (item != null) {
			data.add(i, item);
			rebuild();
		}
	}

	@Override
	public void removeItem(int i) {
		data.remove(i);
		rebuild();
	}

	@Override
	public int getItemCount() {
		return data.size();
	}

	@Override
	public Object getItem(int i) {
		return data.get(i);
	}
	
	protected void rebuild() {
		getComponent().removeAll();
		for (Object o : data) {
			Button button = createButton(o);
			getComponent().add(button);
			button.setSelected(buttons.get(o) != null && buttons.get(o).isSelected());
			buttons.put(o, button);
		}
		fireValueChanged();
	}
}
